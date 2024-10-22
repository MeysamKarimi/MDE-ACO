public class Range
{
    private int low;
    private int high;

    public Range(int low, int high){
        this.low = low;
        this.high = high;
    }

    public boolean contains(int number){
        return (number >= low && number <= high);
    }
    
    public int getMinimum()
    {
    	return low;    	
    }
    
    public int getMaximum()
    {
    	return high;
    }
}