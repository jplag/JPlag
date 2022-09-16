/**
 * Class for testing the C# language module.
 */
public class MyClass
{
    public string  myField = string.Empty;

    public MyClass()
    {
        int i = -1;
    }

    public void MyMethod(int parameter1, string parameter2)
    {
        if(parameter1 == 0) {
            Console.WriteLine("Second parameter {0}", parameter2);
        } else {
            Console.WriteLine("First Parameter {0}, second parameter {1}", 
                                                    parameter1, parameter2);
        }
        
    }

    public int MyAutoImplementedProperty { get; set; }

    private int myPropertyVar;
    
    public int MyProperty
    {
        get { return myPropertyVar; }
        set { myPropertyVar = value; }
    } 
}