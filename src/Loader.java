// -*- Java -*-
/*!
 * @file MobileRobotControllerComp.java
 * @brief Standalone component
 * @date $Date$
 *
 * $Id$
 */

import javax.swing.JOptionPane;

import jp.go.aist.rtm.RTC.CorbaNaming;
import jp.go.aist.rtm.RTC.Manager;
import jp.go.aist.rtm.RTC.ModuleInitProc;
import jp.go.aist.rtm.RTC.RTObject_impl;
import jp.go.aist.rtm.RTC.port.CorbaConsumer;
import jp.go.aist.rtm.RTC.util.CORBA_SeqUtil;
import jp.go.aist.rtm.RTC.util.NVUtil;

import org.omg.CORBA.ORB;

import RTC.ConnectorProfile;
import RTC.ConnectorProfileHolder;
import RTC.ExecutionContextListHolder;
import RTC.LifeCycleState;
import RTC.PortService;
import RTC.RTObject;
import _SDOPackage.NVListHolder;
import _SDOPackage.NameValue;

/*!
 * @class MobileRobotControllerComp
 * @brief Standalone component Class
 *
 */
public class Loader implements ModuleInitProc {

    public void myModuleInit(Manager manager) {
//      Properties prop = new Properties(MobileRobotController.component_conf);
//      mgr.registerFactory(prop, new MobileRobotController(), new MobileRobotController());

        new MobileRobot().registerModule();
        new MobileRobotController().registerModule();
        new VirtualJoystick().registerModule();
        RTObject_impl comp1 = manager.createComponent("MobileRobotController");
        if( comp1==null ) {
      	  System.err.println("Component create failed.");
      	  System.exit(0);
        }
        RTObject_impl comp2 = manager.createComponent("MobileRobot");
        if( comp2==null ) {
      	  System.err.println("Component create failed.");
      	  System.exit(0);
        }
        RTObject_impl comp3 = manager.createComponent("VirtualJoystick");
        if( comp3==null ) {
      	  System.err.println("Component create failed.");
      	  System.exit(0);
        }
        
        
    	/*
      // Create a component
      RTObject_impl comp = mgr.createComponent("MobileRobotController");
      if( comp==null ) {
    	  System.err.println("Component create failed.");
    	  System.exit(0);
      }*/
      
      // Example
      // The following procedure is examples how handle RT-Components.
      // These should not be in this function.

//      // Get the component's object reference
//      Manager manager = Manager.instance();
//      RTObject rtobj = null;
//      try {
//          rtobj = RTObjectHelper.narrow(manager.getPOA().servant_to_reference(comp));
//      } catch (ServantNotActive e) {
//          e.printStackTrace();
//      } catch (WrongPolicy e) {
//          e.printStackTrace();
//      }
//
//      // Get the port list of the component
//      PortListHolder portlist = new PortListHolder();
//      portlist.value = rtobj.get_ports();
//
//      // getting port profiles
//      System.out.println( "Number of Ports: " );
//      System.out.println( portlist.value.length );
//      for( int intIdx=0;intIdx<portlist.value.length;++intIdx ) {
//          Port port = portlist.value[intIdx];
//          System.out.println( "Port" + intIdx + " (name): ");
//          System.out.println( port.get_port_profile().name );
//        
//          PortInterfaceProfileListHolder iflist = new PortInterfaceProfileListHolder();
//          iflist.value = port.get_port_profile().interfaces;
//          System.out.println( "---interfaces---" );
//          for( int intIdx2=0;intIdx2<iflist.value.length;++intIdx2 ) {
//              System.out.println( "I/F name: " );
//              System.out.println( iflist.value[intIdx2].instance_name  );
//              System.out.println( "I/F type: " );
//              System.out.println( iflist.value[intIdx2].type_name );
//              if( iflist.value[intIdx2].polarity==PortInterfacePolarity.PROVIDED ) {
//                  System.out.println( "Polarity: PROVIDED" );
//              } else {
//                  System.out.println( "Polarity: REQUIRED" );
//              }
//          }
//          System.out.println( "---properties---" );
//          NVUtil.dump( new NVListHolder(port.get_port_profile().properties) );
//          System.out.println( "----------------" );
//      }
    }

    public static void activateRTC(ORB orb, String rtcname) throws Exception {
        try {
        	CorbaConsumer<RTObject> corbaConsumer = getRTC(orb, rtcname);
    		
    		ExecutionContextListHolder ecListHolder = new ExecutionContextListHolder();
			ecListHolder.value = new RTC.ExecutionContext[1];
			ecListHolder.value = corbaConsumer._ptr().get_owned_contexts();

			LifeCycleState currentState = ecListHolder.value[0]
					.get_component_state(corbaConsumer._ptr());
			if (!currentState.equals(LifeCycleState.INACTIVE_STATE)) {

				// TODO: Invalid pre-state

				return;
			}

			ecListHolder.value[0].activate_component(corbaConsumer._ptr());

        	
        } catch (Exception e) {
        	System.out.println("Exception:" + e);
        }
    }

    public static CorbaConsumer<RTObject> getRTC(ORB orb, String rtcname) throws Exception {
    	CorbaNaming cn = new CorbaNaming(orb, "localhost:2809");
    	org.omg.CORBA.Object obj;
			obj = cn.resolve(rtcname + ".rtc");
		CorbaConsumer<RTObject> corbaConsumer = new CorbaConsumer<RTObject>(
				RTObject.class);
		corbaConsumer.setObject(obj);
		return corbaConsumer;
    }
    
    public static void deactivateRTC(ORB orb, String rtcname) throws Exception {
        try {
        	CorbaConsumer<RTObject> corbaConsumer = getRTC(orb, rtcname);
    		
    		ExecutionContextListHolder ecListHolder = new ExecutionContextListHolder();
			ecListHolder.value = new RTC.ExecutionContext[1];
			ecListHolder.value = corbaConsumer._ptr().get_owned_contexts();

			LifeCycleState currentState = ecListHolder.value[0]
					.get_component_state(corbaConsumer._ptr());
			if (!currentState.equals(LifeCycleState.INACTIVE_STATE)) {

				// TODO: Invalid pre-state

				return;
			}

			ecListHolder.value[0].deactivate_component(corbaConsumer._ptr());

        	
        } catch (Exception e) {
        	System.out.println("Exception:" + e);
        }
    }
    
    
    public static void connect(ORB orb, String rtc1, String port1, String rtc2, String port2) throws Exception {
    	CorbaConsumer<RTObject> corbaConsumer1 = getRTC(orb, rtc1);
    	CorbaConsumer<RTObject> corbaConsumer2 = getRTC(orb, rtc2);
    	
		ConnectorProfile prof = new ConnectorProfile();
		prof.connector_id = port1 + "_" + port2;
		prof.name = port1 + "_" + port2;
		prof.ports = new PortService[2];
		
		for (PortService portService : (corbaConsumer1._ptr()).get_ports()) {
			if (portService.get_port_profile().name.equals(rtc1 + "." + port1)) {
				prof.ports[1] = portService;
			}
		}
		for (PortService portService : ((RTObject) corbaConsumer2._ptr()).get_ports()) {
			if (portService.get_port_profile().name.equals(rtc2 + "." + port2)) {
				prof.ports[0] = portService;
			}
		}

		NVListHolder nvholder = new NVListHolder();
		nvholder.value = prof.properties;
		if (nvholder.value == null)
			nvholder.value = new NameValue[0];
		CORBA_SeqUtil.push_back(nvholder, NVUtil.newNVString(
				"dataport.interface_type", "corba_cdr"));
		CORBA_SeqUtil.push_back(nvholder, NVUtil.newNVString(
				"dataport.dataflow_type", "push"));
		CORBA_SeqUtil.push_back(nvholder, NVUtil.newNVString(
				"dataport.subscription_type", "new"));
		prof.properties = nvholder.value;

		ConnectorProfileHolder proflist = new ConnectorProfileHolder();
		proflist.value = prof;

		if (prof.ports[0].connect(proflist) != RTC.ReturnCode_t.RTC_OK) {
			throw new Exception("Cannot Connect");
		}
		
    }
    
    public static void main(String[] args) {
        // Initialize manager
        final Manager manager = Manager.init(args);

        // Set module initialization proceduer
        // This procedure will be invoked in activateManager() function.
        //MobileRobotControllerComp init = new MobileRobotControllerComp();
        
        Loader init = new Loader();
        manager.setModuleInitProc(init);

        

        
        // Activate manager and register to naming service
        manager.activateManager();

        // run the manager in blocking mode
        // runManager(false) is the default.
        //manager.runManager();

        // If you want to run the manager in non-blocking mode, do like this
        manager.runManager(true);
        
        try {
	        ORB orb = manager.getORB();
	        connect(orb,"MobileRobot0", "vel", "MobileRobotController0", "vel");
	        connect(orb,"MobileRobot0", "pos", "MobileRobotController0", "pos");
	        connect(orb,"MobileRobot0", "bumper", "MobileRobotController0", "bumper");
	        connect(orb,"VirtualJoystick0", "out", "MobileRobotController0", "joy");
	        
	        activateRTC(orb, "MobileRobot0");
	        activateRTC(orb, "MobileRobotController0");
	        activateRTC(orb, "VirtualJoystick0");
	        
	        
        } catch (Exception e) {
        	System.out.println("Exception : " + e);
        }
    }

}
