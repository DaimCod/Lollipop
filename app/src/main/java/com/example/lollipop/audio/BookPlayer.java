package com.example.lollipop.audio;

import android.media.MediaPlayer;
import java.io.IOException;

public class BookPlayer extends Thread
{
    private String bookUrl;
    private MediaPlayer player;

    public BookPlayer(String bU)
    {
        bookUrl = bU;
        player = new MediaPlayer();
    }

    @Override
    public void run()
    {
        try
        {
            player.setDataSource(bookUrl);
            player.prepare();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void pause()
    {
        player.pause();
    }

    public void play()
    {
        player.start();
    }

    public void reset()
    {
        player.reset();
        player.release();
        player = null;
    }

    public boolean playing()
    {
        return player.isPlaying();
    }

    public MediaPlayer getMediaPlayer()
    {
        return player;
    }
}
