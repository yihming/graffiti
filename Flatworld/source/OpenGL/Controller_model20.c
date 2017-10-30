/*
 *  Controller.c
 *  For the UNM Neural Networks class, this is the only fle you will need to modify.
 *  It contains the agent initialization code and the agent controller code.  An
 *  example of a non-neural controller is included here, which can be modified.
 *  Note that most all of the functions called here can be found in the 
 *  file FlatworldIICore.c
 *  
 *
 *  Created by Thomas Caudell on 9/15/09.
 *  Modified by Thomas Caudell on 9/30/2010
 *  Copyright 2009 UNM. All rights reserved.
 *
 */
#include <sys/stat.h>

float** visual_weight_class;
float** acoustic_weight_class;
int visual_binding_map[3] = {-1};

float angle_locations0[31] = {-15.,-14.,-13.,-12.,-11.,-10.,-9.,-8.,-7.,-6.,-5.,-4.,-3.,-2.,-1.,0.,1.,2.,3.,4.,5.,6.,7.,8.,9.,10.,11.,12.,13.,14.,15} ;

void initial_weight(float**, int , int, char*);
float** normalize_visual(float**);
int find_brightest_eye(float**);
int judge_class_with_neuron(float*);
void set_visual_binding(int*);
float get_intensity(float*);
float check_bound(float headth, float headthmax, float headthmin);
float** filter_visual(float**, float**);
int is_nutrient(float*);
float judge_direction_v2(float**, float**);
float* normalize_vector(float*);
float get_visual_angle_by_index(int);

void init(void)
{ /* This function initializes the graphics, and creates and initializes the world an the agent. tpc */

	AGENT_TYPE *agent ;
	GEOMETRIC_SHAPE_TYPE *agentshape  ;
  ACOUSTIC_SHAPE_TYPE *sound ;
	int nsoundreceptors, nsoundbands ;
  float directions0[31] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0} ;
  time_t now ;
  int i;
  struct tm *date ;
  char timestamp[30] ;
  
	char filename1[100] = "conf/visual_weight_class.conf";
	char filename2[100] = "conf/acoustic_weight_class.conf";


 
	glViewport(0,0,ww,wh) ;
	glMatrixMode( GL_PROJECTION) ;
	glLoadIdentity() ;	
	gluPerspective( frustrum_theta,(GLfloat)ww/(GLfloat)wh,frustrum_znear,frustrum_zfar);
	glMatrixMode(GL_MODELVIEW);
	glClearColor(0.0, 0.0, 0.0, 1.0);
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT ) ;
	glEnable (GL_DEPTH_TEST);
	glShadeModel(GL_SMOOTH) ;
	glEnable(GL_LIGHTING) ;
	
  /* create and initialize the world */
	Flatworld = make_world( 0, 10, 610, 100.0, -100.0, 100.0, -100.0 ) ;
  read_object_spec_file( Flatworld, "WorldObjects.dat" ) ;

	agentshape = read_geometric_shape_file( "geoshapeAgent.dat" ) ;
  sound = read_acoustic_shape_file( "soundshapeAgent.dat" ) ;
  
  nsoundreceptors = sound->nfrequencies ; 
  nsoundbands = sound->nbands ;
    
  /* Creat and initialize the Agent */
  agent = make_agent( 1, 0.0, 0.0, 0.0, 0.5, 1.0 ) ; 
  add_physical_shape_to_agent( agent, agentshape ) ;
  add_sound_shape_to_agent( agent, sound ) ;
	add_visual_sensor_to_agent( agent, 31, 3, 0.0, angle_locations0, directions0 ) ;
  add_acoustic_sensor_to_agent( agent, nsoundreceptors, nsoundbands, 0.0, 90.0 ) ;
  add_acoustic_sensor_to_agent( agent, nsoundreceptors, nsoundbands, 0.0, -90.0 ) ;
  add_cargo_manifest_type_to_agent( agent, 0 ) ;
  add_soma_sensor_to_agent( agent, 1, 0.0, agentshape ) ;
  add_actuators_to_agent( agent, 0.2, 0.0, 0.03, 0.0 ) ;
  set_agent_head_angle( agent, 0.0 ) ;
  set_metabolic_burn_rate_agent(agent, 5.0e-4 ) ;
  
	add_agent_to_world( Flatworld, agent ) ; 
  
  current_agent = agent ;

  /* Initialize all the weight matrices.  */
  visual_weight_class = (float**)malloc(3*sizeof(float*));
  for (i = 0; i < 3; ++i)
	visual_weight_class[i] = (float*)malloc(3*sizeof(float));

  initial_weight(visual_weight_class, 3, 3, filename1);

  
  acoustic_weight_class = (float**)malloc(10*sizeof(float*));
  for (i = 0; i < 10; ++i)
	acoustic_weight_class[i] = (float*)malloc(1*sizeof(float));

  initial_weight(acoustic_weight_class, 10, 1, filename2);

  
  set_visual_binding(visual_binding_map);
  


  /* Initialize the world and wall clock times. */
  init_world_time( Flatworld ) ;
  now = time(NULL) ;
  date = localtime( &now ) ;
  strftime(timestamp, 30, "%y/%m/%d H: %H M: %M S: %S",date) ;
  printf("init- Start time: %s\n",timestamp) ;
}

void agents_controller( WORLD_TYPE *w )
{ /* Adhoc function to test agents, to be replaced with NN controller. tpc */

  OBJECT_TYPE *o ;
  AGENT_TYPE *a ;
  int collision_flag=0 ;
  int i,j,k ;
  int maxvisualreceptor = -1 ;
  float dfb , drl, dth, dh ;
  float headthmax, headthmin, headperiod, headth ;
  float bodyx,bodyy,bodyth, dbodyth ;
  float x, y, h ;
  float rfactor = 0.1, inertia=0.2 ;
  float **eyevalues, **ear0values, **ear1values, **touchvalues ;
  float **normalized_eyevalues;
  float **filtered_eyevalues;
  time_t now ;
  struct tm *date ;
  char timestamp[30] ;


  //float eta = 0.1;

	
	
  /* Initialize head scan ranges and scan peroid */
  headthmax = 160.0 ;
  headthmin = -160.0 ;
  headperiod = 100.0 ;
  
  /* Loop over agents.  For the NN class, there is only one agent */
  for( k=0 ; k<w->nagents ; k++ )
  {
    a = w->agents[k] ; /* get agent pointer */
    
    /* test if agent is alive. if so, process sensors and actuators.  if not, report death and exit */
    if( a->instate->metabolic_charge>0.0 )
    {
      /* get current muscle rates and body/head angles */
      read_actuators_agent( a, &dfb, &drl, &dth, &dh ) ;
      read_agent_body_position( a, &bodyx, &bodyy, &bodyth ) ;
     
 
      /* move the agents body */
      move_body_agent( a ) ;
	move_head_agent(a);

      /* read somatic(touch) sensor for collision */  
      collision_flag = read_soma_sensor(w, a) ;
      if(collision_flag>0 )
      {
        touchvalues = extract_soma_receptor_values_pointer( a ) ;
      }
      //my_eat_with_flag(w, a, collision_flag);
      
/* The following should be replaced with an ALL NEURAL controller */
      if( collision_flag>0 ) 
      {
        /* loop over skin receptor cells */
        for( j=0 ; j<a->instate->skin->nreceptors ; j++ )
        {
          /* get index of colliding object that is in touch with this skin receptor */
          i = a->instate->skin->touched_objects[j] ;
          
          if(i > 0 )  /* the object in collision is an object not an agent, which would return a negative index from this func */
          {
            /* get pointer to object in collision. Object list in world data struct starts at index zero, but function adds one. */
            o = w->objects[i-1] ;
            
            /* test if food object (type=1) and if it can be picked up.  for the NN class, no objects can be picked up,
               so pick up fucntion always returns 0 */
            if( /* o->type==1 && */agent_pickup_object( w, a, o)==0 ) /* pick up object if the manifest has room */
            {
              if( o->inworld_flag!=0) /* test if object in collision is "in the world", i.e. not previously eaten */
              {
                agent_eat_object(  w, a, o ) ; /* eat the object */
                a->instate->itemp[0]++ ; /* increment the total number of objects eaten. this uses one of the user definable 
                                             temp variable in the agent data struct. */
                printf("agent_controller- Object of type: %d eaten. New charge: %f total eaten: %d\n",o->type,a->instate->metabolic_charge,a->instate->itemp[0]) ;

		
		
              }
              //return ;
            }
          }
        }
      }
      
      /* read visual and sound sensors */

      read_visual_sensor( w, a) ;
      eyevalues = extract_visual_receptor_values_pointer( a, 0 ) ;

      // Normalize the visual input and filter it.
      normalized_eyevalues = normalize_visual(eyevalues);

      filtered_eyevalues = filter_visual(eyevalues, normalized_eyevalues);	

	// Find the brightest eye.
      int index = find_brightest_eye(filtered_eyevalues);	


	float objectdirection;

	objectdirection = judge_direction_v2(filtered_eyevalues, eyevalues);

	rotate_agent_head(a, objectdirection);

	read_agent_head_angle(a, &headth);
	headth = check_bound(headth, headthmax, headthmin);

	set_agent_head_angle(a, headth);



	dbodyth = inertia * headth ;
        set_agent_body_angle( a, bodyth+dbodyth ) ;

     
      
      /* decrement metabolic charge by basil metabolism rate.  DO NOT REMOVE THIS CALL */
      basal_metabolism_agent( a ) ;
      
      /* increment world time clock */
      increment_world_clock( w ) ;
      
    } /* end agent alive condition */
    else
    {
      /* Example of agent is dead condition */
      printf("agent_controller- Agent has died, eating %d objects.\n",a->instate->itemp[0]) ;
      print_world_time( Flatworld) ;
      now = time(NULL) ;
      date = localtime( &now ) ;
      strftime(timestamp, 30, "%y/%m/%d H: %H M: %M S: %S",date) ;
      printf("Death time: %s\n",timestamp) ;
      
	free(visual_weight_class);
	free(acoustic_weight_class);
	
      /* Example as to how to restore the world and agent after it dies. */
      //restore_objects_to_world( Flatworld ) ;  /* restore all of the objects h=back into the world */
      //reset_agent_charge( a ) ;               /* recharge the agent's battery to full */
      //a->instate->itemp[0] = 0 ;              /* zero the number of object's eaten accumulator */
      //init_world_time( Flatworld ) ;          /* zero the Flatworld clock */
      //x = distributions_uniform( Flatworld->xmin, Flatworld->xmax ) ; /* pick random starting position and heading */
      //y = distributions_uniform( Flatworld->ymin, Flatworld->ymax ) ;
      //h = distributions_uniform( -179.0, 179.0) ;
      //set_agent_body_position( a, x, y, h ) ;    /* set new position and heading of agent */
      
    } /* end agent dead condition */

  } /* end over all agent loop */
}

/* Initial the weight matrix of a network. */
void initial_weight(float** weight, int len_1, int len_2, char* filename) {
	int i, j;

	if (!access(filename, R_OK)) {
		// File exists.
		FILE *fp = stdin;
		stdin = fopen(filename, "r");
		for (i = 0; i < len_1; ++i)
			for (j = 0; j < len_2; ++j) {
				scanf("%f", &weight[i][j]);
			}	
		
		fclose(stdin);
		stdin = fp;

		
	} else {
		// File doesn't exist.
		
		for (i = 0; i < len_1; ++i)
			for (j = 0; j < len_2; ++j) {
				weight[i][j] = ((float)rand()) / RAND_MAX;
			}
	}
	

} // End of initial_weight().





void set_visual_binding(int* array) {
	char filename[100] = "conf/visual_binding.conf";	

	if (!access(filename, R_OK)) {
		// Configuration file exists.
		FILE* fp = stdin;
		stdin = fopen(filename, "r");	
		scanf("%d%d%d", &array[0], &array[1], &array[2]);
		fclose(stdin);
		stdin = fp;

	} else {
		array[0] = array[1] = array[2] = -1;
	}
}



float** normalize_visual(float** eyevalues) {
	int i, j;

	float** normalized_eyevalues;
	
	float intensity;

	normalized_eyevalues = (float**)malloc(31*sizeof(float*));
	for (i = 0; i < 31; ++i)
		normalized_eyevalues[i] = (float*)malloc(3*sizeof(float));

	for (i = 0; i < 31; ++i) {
		intensity = 0;
		for (j = 0; j < 3; ++j) {
			intensity = intensity + eyevalues[i][j] * eyevalues[i][j];
		}
		intensity = sqrt(intensity);
		
		if (intensity != 0.0) {
			for (j = 0; j < 3; ++j)
				normalized_eyevalues[i][j] = eyevalues[i][j] / intensity;
		}
	}

	return normalized_eyevalues;
}

float* normalize_vector(float* rgb) {
	int i;
	float* normalized_vector;

	float intensity = 0;

	normalized_vector = (float*)malloc(3*sizeof(float));
	for (i = 0; i < 3; ++i)
		intensity = intensity + rgb[i] * rgb[i];
	intensity = sqrt(intensity);

	if (intensity != 0.0) {
		for (i = 0; i < 3; ++i)
			normalized_vector[i] = rgb[i] / intensity;
	}

	return normalized_vector;
}

int find_brightest_eye(float** eyevalues) {
	int i, j;

	float max = 0.0;
	int index = 0;
	float tmp;

	for (i = 0; i < 31; ++i) {
		tmp = 0;
		for (j = 0; j < 3; ++j)
			tmp = tmp + eyevalues[i][j] * eyevalues[i][j];
		if (max < tmp) {
			max = tmp;
			index = i;
		}	
	}	
	

	return index;
}

int judge_class_with_neuron(float* rgb) {
	int i, j;
	int winner_index = 0;
	float max = 0.0;
	float v;

	for (i = 0; i < 3; ++i) {
		v = rgb[0] * visual_weight_class[i][0] + rgb[1] * visual_weight_class[i][1] + rgb[2] * visual_weight_class[i][2];
		if (max < v) {
			max = v;
			winner_index = i;
		}
	}

	return winner_index;
}

float get_intensity(float* rgb) {
	
	return sqrt(rgb[0] * rgb[0] + rgb[1] * rgb[1] + rgb[2] * rgb[2]);
}

float check_bound(float headth, float headthmax, float headthmin) {
	
	if (headth > headthmax) {
		headth = headthmax;
	} else if (headth < headthmin) {
		headth = headthmin;
	}

	return headth;
}

int is_nutrient(float* rgb) {
	int flag = 0;
	int class_index = judge_class_with_neuron(rgb);
	if (visual_binding_map[class_index] == 1) {
		flag = 1;
	}

	return flag;
}


float** filter_visual(float** eyevalues, float** normalized_eyevalues) {
	int i, j;
	float** filtered_eyevalues;

	filtered_eyevalues = (float**)malloc(31*sizeof(float*));
	for (i = 0; i < 31; ++i)
		filtered_eyevalues[i] = (float*)malloc(3*sizeof(float));

	for (i = 0; i < 31; ++i) {
		if (is_nutrient(normalized_eyevalues[i])) {
			// Nutrient.
			for (j = 0; j < 3; ++j)
				filtered_eyevalues[i][j] = eyevalues[i][j];
		} else {
			// Not neutrient. Filtered.
			for (j = 0; j < 3; ++j)
				filtered_eyevalues[i][j] = 0.0;
		}
	}
	
	return filtered_eyevalues;
}

float get_visual_angle_by_index(int index) {

	return angle_locations0[index];
}

float judge_direction_v2(float** filtered_eyevalues, float** eyevalues) {
	float angle;

	// There are objects within eye scope.
	int index = find_brightest_eye(filtered_eyevalues);

	if (get_intensity(filtered_eyevalues[index]) != 0.0) {
		angle = get_visual_angle_by_index(index);
	} else {
		int index = find_brightest_eye(eyevalues);
		if (get_intensity(eyevalues[index]) != 0.0) {
			angle = get_visual_angle_by_index(index);
		} else {
			angle = 30.0;
		}
	}

	return angle;
	
}
