package example.yzhhzq.courseschedule;

import org.json.JSONArray;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hasee on 2016/11/26.
 */
  // This class is used to store the data of each course for the view adapater
public class Course {
    private String Cname;
    private String Cid;
    private java.sql.Time star_time;
    private java.sql.Time end_time;
    private String instructor;
    private java.sql.Date to_data;
    private java.sql.Date from_data;
    private String location;
    private String week;
    private float credit;
    private int CRN;
    private int status;
    private String pre;
    private String booklist;
    public Course()
    {

    }

    public void setBooklist(String booklist) {
        this.booklist = booklist;
    }

    public String getBooklist() {
        return booklist;
    }

    public void setPre(String pre) {
        this.pre = pre;
    }

    public String getPre() {
        return pre;
    }

    public String getCid() {
        return Cid;
    }

    public float getCredit() {
        return credit;
    }

    public int getStatus() {
        return status;
    }

    public int getCRN() {
        return CRN;
    }

    public String getCname() {
        return Cname;
    }

    public java.sql.Date getTo_data() {
        return to_data;
    }

    public java.sql.Date getFrom_data() {
        return from_data;
    }

    public String getInstructor() {
        return instructor;
    }

    public String getLocation() {
        return location;
    }

    public String getWeek() {
        return week;
    }

    public java.sql.Time getEnd_time() {
        return end_time;
    }

    public java.sql.Time getStar_time() {
        return star_time;
    }

    public void setCid(String cid) {
        Cid = cid;
    }

    public void setCname(String cname) {
        Cname = cname;
    }

    public void setEnd_time(String end_time) {
        this.end_time = strToTime(end_time);
    }

    public void setCredit(double credit) {
        this.credit = (float)credit;
    }

    public void setFrom_data(String from_data) {
        this.from_data = strToDate(from_data);
    }

    public void setCRN(int CRN) {
        this.CRN = CRN;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setTo_data(String to_data) {
        this.to_data = strToDate(to_data);
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public void setStar_time(String star_time) {
        this.star_time=strToTime(star_time);

    }
    // method to change the String to Date type
    public static java.sql.Date strToDate(String strDate) {
        String str = strDate;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
        java.util.Date d = null;
        try {
            d = format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        java.sql.Date date = new java.sql.Date(d.getTime());
        return date;
    }
    //method to change the String to Time type
    public static java.sql.Time strToTime(String strDate) {
        String str = strDate;
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        java.util.Date d = null;
        try {
            d = format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        java.sql.Time time = new java.sql.Time(d.getTime());
        return time.valueOf(str);
    }
}
