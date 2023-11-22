package fr.gouv.esante.smt.owl.core;


public class AxiomChanges  {

    
    
    private String type;
    private String old_Value;
    private String new_Value;
    
	public AxiomChanges( String type, String old_Value,
			String new_Value) {
		
		this.type = type;
		this.old_Value = old_Value;
		this.new_Value = new_Value;
	}

	

	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public String getOld_Value() {
		return old_Value;
	}

	public void setOld_Value(String old_Value) {
		this.old_Value = old_Value;
	}

	public String getNew_Value() {
		return new_Value;
	}

	public void setNew_Value(String new_Value) {
		this.new_Value = new_Value;
	}



	
     
    
}

