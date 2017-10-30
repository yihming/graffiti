/* Flatworld II V1.0 main test program.  Doe not use GLUT/OpenGL for sim loop & visualization. 
*  Created 17 March 2009 by T. Caudell
*  Updated 20 Sept 2009, tpc
*  Updated 14 Nov 2009, tpc
*
*  Copyright University of New Mexico 2009
*/

#include <stdio.h>
#include <math.h>
#include <time.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "Distributions_Funcs.h"
#include "FlatworldIICore.h"

#define TRUE 1
#define FALSE 0
#define PI2 6.283185307179586
#define PI 3.141592653589793


/* Global pointer to current Flatworld */
WORLD_TYPE *Flatworld ;
int simtime = 0 ;
int runflag = 1 ;
int execute_flag = 1;


#include "Distributions_Funcs.c"
#include "FlatworldIICore.c"

AGENT_TYPE *current_agent ;

float** visual_weight_class;
float** acoustic_weight_class;

int visual_binding_map[3] = {-1};
int acoustic_binding_map[3] = {-1};

float angle_locations0[31] = {-15.,-14.,-13.,-12.,-11.,-10.,-9.,-8.,-7.,-6.,-5.,-4.,-3.,-2.,-1.,0.,1.,2.,3.,4.,5.,6.,7.,8.,9.,10.,11.,12.,13.,14.,15} ;

float actuator_parameters[4] = {0.2, 0.0, 0.03, 0.0};

int total_eaten;
int eaten_type[3];
float route;

void initial_weight(float**, int , int, char*);
float** normalize_visual(float**);
int find_brightest_eye(float**);
int judge_class_with_visual_network(float*);
void set_visual_binding(int*);
float check_bound(float, float, float);
float get_intensity(float*);
int is_nutrient(float*);
int is_eatable(float**, float**);
float** filter_visual(float**, float**);
float** filter_visual_food(float**, float**);
float get_visual_angle_by_index(int);
//float judge_direction_v2(float**, float**);
float judge_direction_v3(float**, float**, float**, float**, float**);
int whether_to_eat(AGENT_TYPE*,float, float**, float**);
void output_float_matrix(float**, int, int);
int right_collision_edge(float**);

void set_acoustic_binding(int*);
float* get_distance_within_acoustic_network(float**);
int judge_class_with_acoustic_network(float**, float**);
int is_nutrient_by_ear(int);
int is_food_by_eye(float*);
int judge_class_by_single_ear(float**);
float dist_from_nutrient_by_ear(float**);

void init_measure(void);
void write_measure(WORLD_TYPE*);

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
  float **filtered_eyevalues, **normalized_eyevalues, **food_eyevalues;
  time_t now ;
  struct tm *date ;
  char timestamp[30] ;



	
	
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
      //read_agent_head_angle( a, &headth ) ;
     

 
      /* move the agents body */
      move_body_agent( a ) ;
	move_head_agent(a);

	route += dfb;

	set_actuators_agent(a, actuator_parameters[0], actuator_parameters[1], actuator_parameters[2], actuator_parameters[3]);

      /* read somatic(touch) sensor for collision */  
      collision_flag = read_soma_sensor(w, a) ;
      	if (collision_flag > 0) {
		// Read touch information.
		touchvalues = extract_soma_receptor_values_pointer(a);
		

		// Read acoustic information.
		read_acoustic_sensor(w, a);
		ear0values = extract_sound_receptor_values_pointer(a, 0);
		ear1values = extract_sound_receptor_values_pointer(a, 1);

		
		

		// Whether the object is not a poisonous food based on acoustic info.
		int healthy_flag = is_eatable(ear0values, ear1values);

		

		// Whether the collision edge is correct.
		int edge_flag = right_collision_edge(touchvalues);
	
				

		/* loop over skin receptor cells */
        	for( j=0 ; j<a->instate->skin->nreceptors ; j++ )
        	{

          		/* get index of colliding object that is in touch with this skin receptor */
          		i = a->instate->skin->touched_objects[j] ;

          		if(i > 0 )  /* the object in collision is an object not an agent, which would return a negative index from this func */
          		{
            			/* get pointer to object in collision. Object list in world data struct starts at index zero, but function adds one. */
            			o = w->objects[i-1] ;
	
            
              			if( o->inworld_flag!=0) /* test if object in collision is "in the world", i.e. not previously eaten */
              			{

					
					if (healthy_flag && edge_flag) {	

												
						float before_charge = read_agent_metabolic_charge(a);

						if (whether_to_eat(a, before_charge, ear0values, ear1values)) {
							// Eat immediately.
							agent_eat_object(w, a, o);
							++ total_eaten;
							++ eaten_type[o->type - 1];
							float current_charge;
							
							current_charge = read_agent_metabolic_charge(a);
								
	
							//printf("agent_controller- Object of type: %d eaten. New charge: %f total eaten: %d\n",o->type,current_charge,total_eaten);
						} else {
							// Wait for a while.
							

							set_actuators_agent(a, 0.0, 0.0, 0.0, 0.0);
						}
						
				
					}

              			}
             
          		} // End of if (i > 0).
        	} // End of for.

	} // End of if (collision_flag > 0).

      /* read visual and sound sensors */


      read_visual_sensor( w, a) ;
      eyevalues = extract_visual_receptor_values_pointer( a, 0 ) ;
	

	// Normalize the visual input and filter it.
      normalized_eyevalues = normalize_visual(eyevalues);

      filtered_eyevalues = filter_visual(eyevalues, normalized_eyevalues);
	food_eyevalues = filter_visual_food(eyevalues, normalized_eyevalues);	

	// Find the brightest eye.
      int index = find_brightest_eye(filtered_eyevalues);

	read_acoustic_sensor(w, a);
	ear0values = extract_sound_receptor_values_pointer(a, 0);
	ear1values = extract_sound_receptor_values_pointer(a, 1);


	float objectdirection;

	objectdirection = judge_direction_v3(filtered_eyevalues, food_eyevalues, eyevalues, ear0values, ear1values);

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
      //printf("agent_controller- Agent has died, eating %d objects.\n",a->instate->itemp[0]) ;
      //print_world_time( Flatworld) ;
      now = time(NULL) ;
      date = localtime( &now ) ;
      strftime(timestamp, 30, "%y/%m/%d H: %H M: %M S: %S",date) ;
      //printf("Death time: %s\n",timestamp) ;
      
	execute_flag = 0;
	write_measure(w);
	
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




float check_bound(float item, float i_max, float i_min) {
	float y;

	if (item > i_max) {
		y = i_max;
	} else if (item < i_min) {
		y = i_min;
	} else {
		y = item;
	}
	
	return y;
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

int judge_class_with_visual_network(float* rgb) {
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

int is_nutrient(float* rgb) {
	int flag = 0;
	int class_index = judge_class_with_visual_network(rgb);
	if (visual_binding_map[class_index] == 1) {
		flag = 1;
	}

	return flag;
}



int is_eatable(float** ear0values, float** ear1values) {
	int flag = 1;
	int class_index = judge_class_with_acoustic_network(ear0values, ear1values);
	if (acoustic_binding_map[class_index] == 3)
		flag = 0;

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

float judge_direction_v3(float** filtered_eyevalues, float** food_eyevalues, float** eyevalues, float** ear0values, float** ear1values) {
	float angle;

	float threshold = 0.1;

	int index = find_brightest_eye(filtered_eyevalues);

	float intensity = get_intensity(filtered_eyevalues[index]);

	if (intensity != 0.0) {


		// There are nutrient objects within eye scope.
		if (intensity > threshold) {

			// If the brightest object within eye scope is close enough.
			angle = get_visual_angle_by_index(index);

		} else {
			// Consider the acoustic information.
			int class_index_left = judge_class_by_single_ear(ear0values);
			int class_index_right = judge_class_by_single_ear(ear1values);

			if (is_nutrient_by_ear(class_index_left) || is_nutrient_by_ear(class_index_right)) {
				// There is some nutrient object due to ears.
				if (is_nutrient_by_ear(class_index_left)) {
					// It's within left ear.
					//printf("Left Ear!\n");
					angle = -30.0;
				} else {
					// It's within right ear.
					//printf("Right Ear!\n");
					angle = 30.0;
				}

			} else {
				// See food objects.
				angle = get_visual_angle_by_index(index);
			}
		}
	} else {
		// There is no nutrient object within eye scope. Consider ears.
		int class_index_left = judge_class_by_single_ear(ear0values);
		int class_index_right = judge_class_by_single_ear(ear1values);

		if (is_nutrient_by_ear(class_index_left) || is_nutrient_by_ear(class_index_right)) {
			// There is some nutrient object due to ears.
			if (is_nutrient_by_ear(class_index_left)) {
				// It's within left ear.
				//printf("Left Ear!\n");
				angle = -30.0;
			} else {
				// It's within right ear.
				//printf("Right Ear!\n");
				angle = 30.0;
			}

		} else {
						
			float dist_left = dist_from_nutrient_by_ear(ear0values);
			float dist_right = dist_from_nutrient_by_ear(ear1values);

			if (dist_left >= dist_right) {
				angle = -30.0;
			} else {
				angle = 30.0;
			}
		}
	}


	return angle;

} // End of judge_direction_v3.





int whether_to_eat(AGENT_TYPE *a, float current_charge, float** ear0values, float** ear1values) {
	int flag = 1;
	
	float diff = 1.0 - current_charge;

	int class_index = judge_class_with_acoustic_network(ear0values, ear1values);
	if (acoustic_binding_map[class_index] == 1) {
		// It's nutrient.
		if (diff < 0.1) {
			flag = 0;
		}	
	}

	return flag;

}


void output_float_matrix(float** matrix, int dim_x, int dim_y) {
	int i, j;

	for (i = 0; i < dim_x; ++i) {
		for (j = 0; j < dim_y; ++j) {
			printf("%f\t", matrix[i][j]);
		}
		printf("\n");
	}
}

int right_collision_edge(float** touchvalues) {
	int i;
	int flag = 0;

	if (touchvalues[0][0] > 0 || touchvalues[1][0] || touchvalues[7][0])
		flag = 1;

	return flag;
}

void set_acoustic_binding(int* array) {
	char filename[100] = "conf/acoustic_binding_map.conf";

	if (!access(filename, R_OK)) {
		FILE* fid = stdin;
		stdin = fopen(filename, "r");
		scanf("%d%d%d", &array[0], &array[1], &array[2]);

		fclose(stdin);
		stdin = fid;
	} else {
		array[0] = array[1] = array[2] = -1;
	}
	
}

float* get_distance_within_acoustic_network(float** earvalues) {
	int i, j;
	float *distance;
	
	distance = (float*)malloc(3*sizeof(float));

	for (i = 0; i < 3; ++i) {
		distance[i] = 0.0;
		for (j = 0; j < 10; ++j) {
			distance[i] = distance[i] + (earvalues[j][0] - acoustic_weight_class[i][j]) * (earvalues[j][0] - acoustic_weight_class[i][j]);
		}
		distance[i] = sqrt(distance[i]);
	}	
	
	return distance;

}

int judge_class_with_acoustic_network(float** ear0values, float** ear1values) {
	int i;
	float *dist1, *dist2;
	float aver_distance;
	float min_distance = 3000000.0;
	int winner_index = 0;

	dist1 = get_distance_within_acoustic_network(ear0values);
	dist2 = get_distance_within_acoustic_network(ear1values);

	for (i = 0; i < 3; ++i) {
		aver_distance = sqrt(dist1[i] * dist1[i] + dist2[i] * dist2[i]);
		if (aver_distance < min_distance) {
			min_distance = aver_distance;
			winner_index = i;
		}
	}

	return winner_index;
}

int judge_class_by_single_ear(float** earvalues) {
	int i;
	float *dist;
	float min_distance = 3000000.0;
	int winner_index = 0;

	dist = get_distance_within_acoustic_network(earvalues);
	
	for (i = 0; i < 3; ++i) {
		if (dist[i] < min_distance) {
			min_distance = dist[i];
			winner_index = i;
		}
	}

	return winner_index;
}

int is_nutrient_by_ear(int class_index) {
	int flag = 0;

	if (acoustic_binding_map[class_index] == 1) 
		flag = 1;

	return flag;
}

float dist_from_nutrient_by_ear(float** earvalues) {
	float nutrient_dist;
	int i;	
	float* dist;
	int nutrient_index;

	dist = get_distance_within_acoustic_network(earvalues);

	for (i = 0; i < 3; ++i)
		if (acoustic_binding_map[i] == 1)
			nutrient_index = i;

	return dist[nutrient_index];	
	
}

int is_food_by_eye(float* rgb) {
	int flag = 0;
	int class_index;

	class_index = judge_class_with_visual_network(rgb);
	if (visual_binding_map[class_index] == 2)
		flag = 1;

	return flag;
}

float** filter_visual_food(float** eyevalues, float** normalized_eyevalues) {
	int i, j;
	float** food_eyevalues;

	food_eyevalues = (float**)malloc(31*sizeof(float*));
	for (i = 0; i < 31; ++i)
		food_eyevalues[i] = (float*)malloc(3*sizeof(float));

	for (i = 0; i < 31; ++i) {
		if (is_food_by_eye(normalized_eyevalues[i])) {
			for (j = 0; j < 3; ++j)
				food_eyevalues[i][j] = eyevalues[i][j];
		} else {
			for (j = 0; j < 3; ++j)
				food_eyevalues[i][j] = 0.0;
		}
	}

	return food_eyevalues;
}

void init_measure(void) {
	int i;
	
	total_eaten = 0;
	for (i = 0; i < 3; ++i)
		eaten_type[i] = 0;	
	route = 0;
}

void write_measure(WORLD_TYPE *w) {
	//FILE* fid;
	//char filename[100] = "results/model_27.out";
	
	unsigned long int sec = seconds_from_start(w);

	//fid = fopen(filename, "a+");
	//fprintf(fid, "%d\t%ld\t%d\t%d\t%d\t%d\t%f\n", num_exp, sec, total_eaten, eaten_type[0], eaten_type[1], eaten_type[2], route);
	printf("%ld\t%d\t%d\t%d\t%d\t%f\n", sec, total_eaten, eaten_type[0], eaten_type[1], eaten_type[2], route);	

	//fclose(fid);
}

/* Main Loop ---------------------------------------------------------------------------------------*/
int main(int argc, char** argv)
{
	AGENT_TYPE *agent ;
	GEOMETRIC_SHAPE_TYPE *agentshape  ;
  ACOUSTIC_SHAPE_TYPE *sound ;
	int nsoundreceptors, nsoundbands ;
  int t ;
	int i;
  float angle_locations0[31] = {-15.,-14.,-13.,-12.,-11.,-10.,-9.,-8.,-7.,-6.,-5.,-4.,-3.,-2.,-1.,0.,1.,2.,3.,4.,5.,6.,7.,8.,9.,10.,11.,12.,13.,14.,15} ;
  float directions0[31] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0} ;
  time_t now ;
  struct tm *date ;
  char timestamp[30] ;

	char filename1[100] = "conf/visual_weight_class.conf";
	char filename2[100] = "conf/acoustic_weight_class.conf";

 /* create and initialize the world */
  //printf("main- making and loading world.\n") ;
	Flatworld = make_world( 0, 10, 610, 100.0, -100.0, 100.0, -100.0 ) ;
  read_object_spec_file( Flatworld, "WorldObjects.dat" ) ;

	agentshape = read_geometric_shape_file( "geoshapeAgent.dat" ) ;
  sound = read_acoustic_shape_file( "soundshapeAgent.dat" ) ;
  
  nsoundreceptors = sound->nfrequencies ; 
  nsoundbands = sound->nbands ;
    
  /* Creat and initialize the Agent */
  //printf("main- making and atrributing agent.\n") ;
  agent = make_agent( 1, 0.0, 0.0, -30.0, 0.5, 1.0 ) ; 
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

	int x = distributions_uniform( Flatworld->xmin, Flatworld->xmax ) ; /* pick random starting position and heading */
      	int y = distributions_uniform( Flatworld->ymin, Flatworld->ymax ) ;
      	int h = distributions_uniform( -179.0, 179.0) ;
      	set_agent_body_position( agent, x, y, h ) ;    /* set new position and heading of agent */

/* Initialize all the weight matrices.  */
  visual_weight_class = (float**)malloc(3*sizeof(float*));
  for (i = 0; i < 3; ++i)
	visual_weight_class[i] = (float*)malloc(3*sizeof(float));

  initial_weight(visual_weight_class, 3, 3, filename1);

  
  acoustic_weight_class = (float**)malloc(3*sizeof(float*));
  for (i = 0; i < 3; ++i)
	acoustic_weight_class[i] = (float*)malloc(10*sizeof(float));	

  initial_weight(acoustic_weight_class, 3, 10, filename2);

	

  
	set_visual_binding(visual_binding_map);
	set_acoustic_binding(acoustic_binding_map);

	init_measure();

  /* Initialize the world and wall clock times. */
  init_world_time( Flatworld ) ;
  now = time(NULL) ;
  date = localtime( &now ) ;
  strftime(timestamp, 30, "%y/%m/%d H: %H M: %M S: %S",date) ;
  //printf("main- Start time: %s\n",timestamp) ;

  for( t=0 ; t<100000 ; t++ )
  {
	if (execute_flag) {
		agents_controller( Flatworld ) ;
	}
    
  }

  //printf("main- terminating normally.\n") ;
}

