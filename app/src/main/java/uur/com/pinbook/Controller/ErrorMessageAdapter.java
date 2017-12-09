package uur.com.pinbook.Controller;


/**
 * Created by mac on 4.12.2017.
 */

public enum ErrorMessageAdapter{

    //  Add Error Tags here -with next number
    EMAIL_EMPTY(101),
    PASSWORD_EMPTY(102),
    INVALID_USER(103);

    public final int number;

    private ErrorMessageAdapter(int number) {
        this.number = number;
    }

    public int getNumber(){
        return number;
    }

    // Add Error Code explanation here..
    public String getText() {

        String s;

        switch(number) {
            case 101:
                s = "Email can't be empty";
                break;
            case 102:
                s = "Password can't be empty";
                break;
            case 103:
                s = "User is invalid";
                break;
            case 104:
                s = "";
                break;
            default:
                s = "Unknown Error";


        }

        return s;
    }

    public static void setError(int number){
        return;
    }


}