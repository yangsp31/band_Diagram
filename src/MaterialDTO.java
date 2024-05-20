public class MaterialDTO {
    private String Type;
    private String Name;
    private double HighValue;
    private double LowValue;

    public MaterialDTO (String type, String name) {
        this.Name = name;
        this.Type = type;
        this.LowValue = 0;
        this.HighValue = 0;
    }

    public void setValue(double highValue) {
        if(highValue == 0) {
            this.HighValue = highValue * -1;
        }
        else {
            this.HighValue = highValue;
        }
    }

    public void setValue(double highValue, double lowValue) {
        if(highValue == 0) {
            this.HighValue = highValue * -1;
            this.LowValue = lowValue;
        }
        else {
            this.HighValue = highValue;
            this.LowValue = lowValue;
        }
    }

    public String getType() {
        return this.Type;
    }

    public String getName() {
        return this.Name;
    }

    public double getHighValue() {
        return this.HighValue;
    }

    public double getLowValue() {
        return this.LowValue;
    }
}
