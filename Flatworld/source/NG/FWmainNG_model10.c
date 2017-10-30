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

#include "Distributions_Funcs.c"
#include "FlatworldIICore.c"

AGENT_TYPE *current_agent ;

void agents_controller( WORLD_TYPE *w )
{ /* Adhoc function to test agents, to be replaced with NN controller. tpc */

  OBJECT_TYPE *o ;
  AGENT_TYPE *a ;
  int collision_flag=0 ;
  int i,j,k ;
  int maxvisualreceptor = -1 ;
  float dfb , drl, dth, dh ;
  float headthmax, headthmin, headperiod, headth ;
  float objectdirection ;
  float bodyx,bodyy,bodyth, dbodyth ;
  float x, y, h ;
  float rfactor = 0.1, inertia=0.2 ;
  float **eyevalues, **ear0values, **ear1values, **touchvalues ;
  time_t now ;
  struct tm *date ;
  char timestamp[30] ;
  
  /* Initialize hed scan ranges and scan peroid */
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
      read_agent_head_angle( a, &headth ) ;
      
      /* move the agents body */
      move_body_agent( a ) ;

      /* read somatic(touch) sensor for collision */  
      collision_flag = read_soma_sensor(w, a) ;
      if(collision_flag>0 )
      {
        touchvalues = extract_soma_receptor_values_pointer( a ) ;
      }
      
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
                printf("agent_controller- Object of type: %d eaten. New charge: %f total eaten: %d simtime: %d\n",o->type,a->instate->metabolic_charge,a->instate->itemp[0],simtime) ;
              }
              //return ;
            }
          }
        }
      }
      
      /* read visual and sound sensors */
      read_visual_sensor( w, a) ;
      eyevalues = extract_visual_receptor_values_pointer( a, 0 ) ;
      read_acoustic_sensor( w, a) ;
      ear0values = extract_sound_receptor_values_pointer( a, 0 ) ;
      ear1values = extract_sound_receptor_values_pointer( a, 1 ) ;

/* The following should be replaced with an ALL NEURAL controller */
      process_visual_sensors_1( a, &maxvisualreceptor ) ;
      if( maxvisualreceptor < 0 ) 
      {
        /* scan head, if there are no objects in range of visual sensor */
        scan_head_agent( a, headthmax, headthmin, headperiod ) ;
        set_agent_body_angle( a, bodyth + distributions_uniform( -rfactor, rfactor )) ;
      }
      else
      {
        /* use brightest visual receptor to determine how to turn head to center it in the field of view */
        objectdirection = visual_receptor_position( a->instate->eyes[0], maxvisualreceptor ) ;
        
        /* rotate head to center brightest object in center of field of view */
        rotate_agent_head( a, objectdirection ) ;
        
        /* get new head direction and test if its in bounds of head direction (wrt the body) max and min */
        read_agent_head_angle( a, &headth ) ;
        if( headth>headthmax )
          headth= headthmax ;
        if( headth<headthmin )
          headth = headthmin ;
        
        /* enforce head angle being in scan range */
        set_agent_head_angle( a, headth ) ;
        
        /* compute change in body direction wrt world that will reduce head direction angle wrt the body.  Inertia causes 
           this change in body direction wrt world to be gradual. this action slowly rotates the body to point in the 
           same direction the head is looking (wrt world) */
        dbodyth = inertia * headth ;
        set_agent_body_angle( a, bodyth+dbodyth ) ;
      }
      
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
      
      /* Example as to how to restore the world and agent after it dies. */
      restore_objects_to_world( Flatworld ) ;  /* restore all of the objects h=back into the world */
      reset_agent_charge( a ) ;               /* recharge the agent's battery to full */
      a->instate->itemp[0] = 0 ;              /* zero the number of object's eaten accumulator */
      init_world_time( Flatworld ) ;          /* zero the Flatworld clock */
      x = distributions_uniform( Flatworld->xmin, Flatworld->xmax ) ; /* pick random starting position and heading */
      y = distributions_uniform( Flatworld->ymin, Flatworld->ymax ) ;
      h = distributions_uniform( -179.0, 179.0) ;
      set_agent_body_position( a, x, y, h ) ;    /* set new position and heading of agent */
      
    } /* end agent dead condition */

  } /* end over all agent loop */
  
}

/* Main Loop ---------------------------------------------------------------------------------------*/
int main(int argc, char** argv)
{
	AGENT_TYPE *agent ;
	GEOMETRIC_SHAPE_TYPE *agentshape  ;
  ACOUSTIC_SHAPE_TYPE *sound ;
	int nsoundreceptors, nsoundbands ;
  int t ;
  float angle_locations0[31] = {-15.,-14.,-13.,-12.,-11.,-10.,-9.,-8.,-7.,-6.,-5.,-4.,-3.,-2.,-1.,0.,1.,2.,3.,4.,5.,6.,7.,8.,9.,10.,11.,12.,13.,14.,15} ;
  float directions0[31] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0} ;
  time_t now ;
  struct tm *date ;
  char timestamp[30] ;

 /* create and initialize the world */
  printf("main- making and loading world.\n") ;
	Flatworld = make_world( 0, 10, 610, 100.0, -100.0, 100.0, -100.0 ) ;
  read_object_spec_file( Flatworld, "WorldObjects.dat" ) ;

	agentshape = read_geometric_shape_file( "geoshapeAgent.dat" ) ;
  sound = read_acoustic_shape_file( "soundshapeAgent.dat" ) ;
  
  nsoundreceptors = sound->nfrequencies ; 
  nsoundbands = sound->nbands ;
    
  /* Creat and initialize the Agent */
  printf("main- making and atrributing agent.\n") ;
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

  /* Initialize the world and wall clock times. */
  init_world_time( Flatworld ) ;
  now = time(NULL) ;
  date = localtime( &now ) ;
  strftime(timestamp, 30, "%y/%m/%d H: %H M: %M S: %S",date) ;
  printf("main- Start time: %s\n",timestamp) ;

  for( t=0 ; t<100000 ; t++ )
  {
    agents_controller( Flatworld ) ;
    simtime++ ;
  }

  printf("main- terminating normally.\n") ;
}

