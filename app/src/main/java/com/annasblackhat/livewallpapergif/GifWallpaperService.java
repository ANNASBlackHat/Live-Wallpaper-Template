package com.annasblackhat.livewallpapergif;

import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.io.IOException;

public class GifWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        try {
            Movie movie = Movie.decodeStream(getResources().getAssets().open("gif_image.gif"));
            return new GifWallpaperEngine(movie);
        } catch (IOException e) {
            System.out.println("xxx image not found.... "+e);
        }
        return null;
    }

    private class GifWallpaperEngine extends Engine{

        private final int frameDuration = 20;
        private SurfaceHolder holder;
        private Movie movie;
        private boolean visible;
        private Handler handler;

        public GifWallpaperEngine(Movie movie) {
            this.movie = movie;
            handler = new Handler();
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            this.holder = surfaceHolder;
        }
        
        private Runnable drawGif = new Runnable() {
            @Override
            public void run() {
                draw();
            }
        };

        private void draw() {
            if(visible){
                try {
                    Canvas canvas = holder.lockCanvas();
                    canvas.save();
                    canvas.scale(3f, 3f);
                    movie.draw(canvas, -100, 0);
                    canvas.restore();
                    holder.unlockCanvasAndPost(canvas);
                    int duration = movie.duration() > 0 ? movie.duration() : 1;
                    movie.setTime((int) (System.currentTimeMillis() % duration));

                    handler.removeCallbacks(drawGif);
                    handler.postDelayed(drawGif, frameDuration);
                } catch (Exception e) {
                    System.out.println("xxx error "+e);
                }
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if(visible){
                handler.post(drawGif);
            }else{
                handler.removeCallbacks(drawGif);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(drawGif);
        }
    }
}
