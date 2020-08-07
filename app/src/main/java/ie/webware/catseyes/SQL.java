package ie.webware.catseyes;
// All sql statements go here so they can be converted to lambda expressions
public class SQL
{
	private SQL(){}
    private static final SQL instance = new SQL();
    public static SQL getInstance(){
        return instance;
    }
};
