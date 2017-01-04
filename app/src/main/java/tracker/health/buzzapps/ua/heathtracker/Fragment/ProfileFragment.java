package tracker.health.buzzapps.ua.heathtracker.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import tracker.health.buzzapps.ua.heathtracker.MainActivity;
import tracker.health.buzzapps.ua.heathtracker.Model.User;
import tracker.health.buzzapps.ua.heathtracker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment  {

    User currentUser;
    public ProfileFragment() {
        currentUser = MainActivity.getCurrentUser();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.user_image);
        TextView nameTextView = (TextView)view.findViewById(R.id.user_name);
        TextView emailTextView = (TextView)view.findViewById(R.id.user_email);
        TextView teamTextView = (TextView)view.findViewById(R.id.teamTextView);
        Button changeTeamButton = (Button) view.findViewById(R.id.changeTeamButton);

        nameTextView.setText(currentUser.getName());
        emailTextView.setText(currentUser.getEmail());
        teamTextView.setText(currentUser.getTeam());
        Glide.with(this).load(currentUser.getPhotoUrl()).into(imageView);

        return view;
    }

}
