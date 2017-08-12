package com.example.pebblesappv2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlayerBarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayerBarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerBarFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SONGTITLE = "param1";
//    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
//    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ImageButton playBtn;
    private ImageButton nextBtn;
    private ImageButton backBtn;
    private ImageButton cancelBtn;

    public PlayerBarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment PlayerBarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayerBarFragment newInstance(String param1) {
        PlayerBarFragment fragment = new PlayerBarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SONGTITLE, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_SONGTITLE);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player_bar, container, false);

        TextView titleTV = (TextView) view.findViewById(R.id.showPlaying);
        titleTV.setText("Now Playing: "+mParam1+" ...");

        playBtn = (ImageButton) view.findViewById(R.id.playbutton);
        nextBtn = (ImageButton) view.findViewById(R.id.playnextbutton);
        backBtn = (ImageButton) view.findViewById(R.id.playbackbutton);
        cancelBtn = (ImageButton) view.findViewById(R.id.cancelbutton);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onPlayButtonClicked();
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onNextButtonClicked();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onBackButtonClicked();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCancelButtonClicked();
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onCancelButtonClicked();
        public void onPlayButtonClicked();
        public void onBackButtonClicked();
        public void onNextButtonClicked();
    }
}
