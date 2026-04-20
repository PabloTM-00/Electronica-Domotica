package practicas_eldom.config;

public class MB_Registers {
	
	//Discrete Output Coils
	public static final int MB_RELE 	= 0;
	public static final int MB_TRIAC 	= 1;
	public static final int MB_O_COILS	= 2;
	
	public enum TSwitchState { 
		OFF, 
		ON,  
		TOGGLE;
		
		public static String ToNumberString(TSwitchState x) {
	        switch(x) {
	        case OFF:
	            return "0";
	        case ON:
	        	return "1";
	        case TOGGLE:
	        	return "2";
	        default:
	        	return "0";
	        }
	    }
		
	}
	
}
