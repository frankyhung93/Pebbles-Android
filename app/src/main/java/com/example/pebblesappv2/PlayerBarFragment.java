package com.example.pebblesappv2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Html;
import android.util.Log;
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
    private static final String ARG_PLAYTYPE = "param1";
    private static final String ARG_SONGTITLE = "param2";
    private static final String ARG_ACTIVITY = "param3";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;

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
     * @param param2 Parameter 2.
     * @param param3 Parameter 3.
     * @return A new instance of fragment PlayerBarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayerBarFragment newInstance(String param1, String param2, String param3) {
        PlayerBarFragment fragment = new PlayerBarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLAYTYPE, param1);
        args.putString(ARG_SONGTITLE, param2);
        args.putString(ARG_ACTIVITY, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PLAYTYPE);
            mParam2 = getArguments().getString(ARG_SONGTITLE);
            mParam3 = getArguments().getString(ARG_ACTIVITY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player_bar, container, false);

        TextView titleTV = (TextView) view.findViewById(R.id.showPlaying);
        String barTitleText = "";
        if (mParam1.equals(AlbumPlayList.TYPE_PLAY)) {
            if (mParam3.equals("AlbumPlayList"))
                barTitleText = "<font color=#ffffff>Now Playing from</font> <font color=#0aff9d>"+((AlbumPlayList)getActivity()).returnPlayingAlbum()+"</font><font color=#ffffff>: "+mParam2+" ...</font>";
            else if (mParam3.equals("SearchResultsActivity"))
                barTitleText = "<font color=#ffffff>Now Playing from</font> <font color=#0aff9d>"+((SearchResultsActivity)getActivity()).returnPlayingAlbum()+"</font><font color=#ffffff>: "+mParam2+" ...</font>";
        } else if (mParam1.equals(AlbumPlayList.TYPE_SHUFFLE)) {
            if (mParam3.equals("AlbumPlayList"))
                barTitleText = "<font color=#ffffff>Now Shuffling from</font> <font color=#0aff9d>"+((AlbumPlayList)getActivity()).returnPlayingAlbum()+"</font><font color=#ffffff>: "+mParam2+" ...</font>";
            else if (mParam3.equals("SearchResultsActivity"))
                barTitleText = "<font color=#ffffff>Now Shuffling from</font> <font color=#0aff9d>"+((SearchResultsActivity)getActivity()).returnPlayingAlbum()+"</font><font color=#ffffff>: "+mParam2+" ...</font>";
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            titleTV.setText(Html.fromHtml(barTitleText, Html.FROM_HTML_MODE_LEGACY));
        } else {
            titleTV.setText(Html.fromHtml(barTitleText));
        }

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
