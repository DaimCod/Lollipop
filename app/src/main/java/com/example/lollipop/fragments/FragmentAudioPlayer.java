package com.example.lollipop.fragments;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.lollipop.R;
import com.example.lollipop.Utils.Constants;
import com.example.lollipop.Utils.InternetConnection;
import com.example.lollipop.viewmodels.ViewModelBookPlayer;
import com.example.lollipop.viewmodels.ViewModelBooks;
import com.example.lollipop.viewmodels.ViewModelUser;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class FragmentAudioPlayer extends Fragment implements View.OnClickListener, View.OnLongClickListener
{
    private View view;
    private ViewModelBooks viewModelBooks;
    private ImageView playPause, rewind, forward;
    private ViewModelBookPlayer viewModelBookPlayer;
    private SeekBar seekBarBook, seekBarVolume;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private ViewModelUser viewModelUser;
    private TextView timeElapsed, timeRemained;
    private ProgressBar loading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view =  inflater.inflate(R.layout.fragment_fragmet_audio_player, container, false);
        getActivity().findViewById(R.id.button_settings).setVisibility(View.GONE);

        viewModelBooks = new ViewModelProvider(requireActivity()).get(ViewModelBooks.class);
        viewModelBookPlayer = new ViewModelProvider(requireActivity()).get(ViewModelBookPlayer.class);
        viewModelUser = new ViewModelProvider(requireActivity()).get(ViewModelUser.class);

        timeElapsed = view.findViewById(R.id.timeElapsed);
        timeRemained = view.findViewById(R.id.timeRemained);
        playPause = view.findViewById(R.id.playPause);
        seekBarBook = view.findViewById(R.id.bookProgress);
        seekBarVolume = view.findViewById(R.id.volumeAdjuster);
        rewind = view.findViewById(R.id.rewind);
        forward = view.findViewById(R.id.forward);
        loading = view.findViewById(R.id.loadingPlayer);

        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //RELOAD
        if(viewModelBookPlayer.getCurrentTrack().equalsIgnoreCase(viewModelBooks.getSelected().getNameOnServer()))
        {
            if(viewModelBookPlayer.isBookPlaying())
            {
                playPause.setTag(R.drawable.ic_pause);
                playPause.setImageResource(R.drawable.ic_pause);
                setUi();
                seekBarUpdater(mediaPlayer.getDuration());
            }
            else
            {
                playPause.setTag(R.drawable.ic_play);
                playPause.setImageResource(R.drawable.ic_play);
                setUi();
            }
            seekTo();
            enableClickListener();
            enableFragment();
        }
        else if(InternetConnection.isConnected(requireActivity()))
        {
            final Observer<String> observeUrl = bU ->
            {
                viewModelBookPlayer.destroyPlayer();
                viewModelBookPlayer.initializeBookPlayer(bU, viewModelBooks.getSelected().getNameOnServer());

                playPause.setTag(R.drawable.ic_play);
                setUi();
                prepareStart();
                seekTo();
            };

            viewModelBooks.setSelectedUrl(getString(R.string.author_spacer), viewModelBooks.getSelected().getNameOnServer());
            viewModelBooks.observeBookUrl().observe(getViewLifecycleOwner(), observeUrl);
        }
        else
        {
            Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
        }
        return view;
    }

    //IMPLEMENTARE NEXT BOOK, WHY?
    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.playPause)
        {
            if(getDrawableId((ImageView) v) == R.drawable.ic_play)
            {
                ((ImageView) v).setImageResource(R.drawable.ic_pause);
                v.setTag(R.drawable.ic_pause);

                viewModelBookPlayer.playBook();
                seekBarUpdater(mediaPlayer.getDuration());
            }
            else
            {
                stopPlayer();
            }
        }
        else if(v.getId() == R.id.rewind)
        {
            mediaPlayer.seekTo(Math.max(mediaPlayer.getCurrentPosition() - 10000, 0));
        }
        else if(v.getId() == R.id.forward)
        {
            mediaPlayer.seekTo(Math.min(mediaPlayer.getCurrentPosition() + 10000, mediaPlayer.getDuration()));
        }
    }

    @Override
    public boolean onLongClick(View v)
    {
        //PASSARE A LIBRO SUCCESSIVO?? I DON'T KNOW...
        if(v.getId() == R.id.forward)
        {
            mediaPlayer.seekTo(mediaPlayer.getDuration());
        }
        else if(v.getId() == R.id.rewind)
        {
            mediaPlayer.seekTo(0);
        }
        return true;
    }

    private void stopPlayer()
    {
        playPause.setImageResource(R.drawable.ic_play);
        playPause.setTag(R.drawable.ic_play);
        viewModelBookPlayer.pauseBook();
    }

    private int getDrawableId(ImageView iV)
    {
        return (Integer) iV.getTag();
    }


    private void setUi()
    {
        TextView bookTitle = view.findViewById(R.id.book_title);
        bookTitle.setText(viewModelBooks.getSelected().getTitle());

        ImageView cover = view.findViewById(R.id.cover);
        Picasso.get().load(viewModelBooks.getSelected().getUrl().toString())
                .resize(200, 275)
                .into(cover);

        mediaPlayer = viewModelBookPlayer.getBookPlayer();
        volumeAdjuster();
    }

    //ENABLE USER TO CHANGE POSITION
    private void seekTo()
    {
        seekBarBook.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    //SET VOLUME ADJUSTER CONTROL
    private void volumeAdjuster()
    {
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        seekBarVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekBarVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if(fromUser)
                {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {}
        });
    }

    //WAIT TILL MEDIA PLAYER IS PREPARED
    private void prepareStart()
    {
        mediaPlayer.setOnPreparedListener(mp ->
        {
            ArrayList<String> readPos = viewModelUser.getUser().getReadPos();
            ArrayList<String> readBooks = viewModelUser.getUser().getBooksRead();

            for(int i = 0; i < readBooks.size(); ++i)
            {
                if(readBooks.get(i).equalsIgnoreCase(viewModelBooks.getSelected().getNameOnServer()))
                {
                    seekBarBook.setMax(mp.getDuration());

                    String [] split = readPos.get(i).split(Constants.SPLITTER_DURATION);
                    mp.seekTo(Integer.parseInt(split[0]));

                    seekBarBook.setProgress(mp.getCurrentPosition());
                    timeElapsed.setText(millisecondsParser(mp.getCurrentPosition()));
                    String text = "-" + millisecondsParser(mp.getDuration() - mp.getCurrentPosition());
                    timeRemained.setText(text);
                    break;
                }
            }
            enableClickListener();
            enableFragment();
        });
    }

    //ENABLE CLICK LISTENER
    private void enableClickListener()
    {
        playPause.setOnClickListener(this);
        rewind.setOnClickListener(this);
        forward.setOnClickListener(this);
        rewind.setOnLongClickListener(this);
        forward.setOnLongClickListener(this);
    }

    //SET UP TIME TO SHOW
    private void seekBarUpdater(int duration)
    {
        seekBarBook.setMax(duration);
        timeRemained.setText(millisecondsParser(duration - mediaPlayer.getCurrentPosition()));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    seekBarBook.setProgress(mediaPlayer.getCurrentPosition());
                    timeElapsed.setText(millisecondsParser(mediaPlayer.getCurrentPosition()));
                    String timeRem =  "-" + millisecondsParser(duration  - mediaPlayer.getCurrentPosition());
                    timeRemained.setText(timeRem);
                    handler.postDelayed(this, 1000);
                }
                catch (Exception e)
                {
                    seekBarBook.setProgress(0);
                }
            }
        }, 0);
    }

    private String millisecondsParser(int duration)
    {
        StringBuilder toReturn = new StringBuilder();
        int hours = duration / 3600000;
        int minutes = (duration - hours * 3600000) / 60000;
        int seconds = (duration - hours * 3600000 - minutes * 60000) / 1000;

        if(hours > 0)
        {
            toReturn.append(hours).append(":");

            if(minutes < 10)
            {
                toReturn.append(0);
            }
        }

        if(seconds < 10)
        {
            toReturn.append(minutes).append(":0").append(seconds);
        }
        else
        {
            toReturn.append(minutes).append(":").append(seconds);
        }
        return toReturn.toString();
    }

    private void enableFragment()
    {
        loading.setVisibility(View.GONE);
        view.findViewById(R.id.layoutPlayer).setClickable(true);
    }
}