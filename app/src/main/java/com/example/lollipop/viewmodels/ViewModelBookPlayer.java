package com.example.lollipop.viewmodels;

import android.app.Application;
import android.media.MediaPlayer;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.example.lollipop.audio.BookPlayer;

public class ViewModelBookPlayer extends AndroidViewModel
{
    private BookPlayer bookPlayer;
    private boolean currentlyPlaying;
    private String nameOnServer;

    public ViewModelBookPlayer(@NonNull Application application)
    {
        super(application);
        bookPlayer = null;
        currentlyPlaying = false;
        nameOnServer = "";
    }

    public void initializeBookPlayer(String bU, String title)
    {
        bookPlayer = new BookPlayer(bU);
        currentlyPlaying = true;
        nameOnServer = title;
        bookPlayer.start();
    }

    public void pauseBook()
    {
        bookPlayer.pause();
    }

    public void playBook()
    {
        bookPlayer.play();
    }

    public void destroyPlayer()
    {
        if(currentlyPlaying)
        {
            bookPlayer.reset();
            bookPlayer.interrupt();
            currentlyPlaying = false;
            nameOnServer = "";
            bookPlayer = null;
        }
    }
    public BookPlayer getPlayerGeneral() { return bookPlayer; }

    public String getCurrentTrack()
    {
        return nameOnServer;
    }

    public boolean isBookPlaying()
    {
        return bookPlayer.playing();
    }

    public MediaPlayer getBookPlayer()
    {
        return bookPlayer.getMediaPlayer();
    }
}
