package example.yzhhzq.courseschedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

// Course type adapter to fill the search_ListView and remind_listView
public class ListViewAdapter extends ArrayAdapter<Course> {
    private int resourceId;
    private int swtich;
    public ListViewAdapter(Context context, int textViewResourceId,
                        List<Course> objects,int swtich) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        this.swtich=swtich;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Course course = getItem(position); // get the current item of Course
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        if(swtich==1) {
            TextView Cname=(TextView) view.findViewById(R.id.s_Cname);
            TextView Cid=(TextView) view.findViewById(R.id.s_Cid);
            TextView Instructor=(TextView) view.findViewById(R.id.s_Instructor);
            Cname.setText(course.getCname());
            Cid.setText(course.getCid());
            Instructor.setText(" Instructor：" + course.getInstructor());
        }
        if(swtich==0)
        {
            int judge;
            TextView Cname=(TextView) view.findViewById(R.id.s_Cname);
            TextView Instructor=(TextView) view.findViewById(R.id.s_Cid);
            TextView Status=(TextView) view.findViewById(R.id.s_Instructor);
            Cname.setText(course.getCname());
            Instructor.setText(" Instructor：" + course.getInstructor());
            //according to the status to set the text
            judge=course.getStatus();
            if(judge==0)  Status.setText("Status: Closed");
            if(judge==1)  Status.setText("Status: Open");
            if(judge==2)  Status.setText("Status: Cancled");
        }
        return view;
    }





}
