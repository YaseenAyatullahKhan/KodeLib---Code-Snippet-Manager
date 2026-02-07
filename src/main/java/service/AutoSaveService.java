package service;

import java.util.Timer;
import java.util.TimerTask;

import model.Version;
import util.DateTimeUtil;

public class AutoSaveService {
    //for starting timer and returning true if already 5 minutes
    private Timer timer;
    private String filename;
    private SnippetEditorGetter snippetGetter;
    
    //interface class to get current snippet from controller
    public interface SnippetEditorGetter {
        Version getCurrentSnippet();
    }
    
    public void startTimer(String filename, SnippetEditorGetter getter) {
        this.filename = filename;
        this.snippetGetter = getter;
        
        if (timer != null) {
            timer.cancel();
        }
        
        timer = new Timer(true);
        //creating low-prority (daemon) thread in background to not block main GUI thread
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                String timestamp = DateTimeUtil.getFormattedDateTimeNow();
                System.out.println("[" + timestamp + "] Auto-saving: " + filename);
                if (snippetGetter != null) {
                    Version currentSnippet = snippetGetter.getCurrentSnippet();
                    SnippetService.update(filename, currentSnippet);
                }
            }
        };
        // Schedule to run every 5 minutes (300000ms)
        timer.scheduleAtFixedRate(task, 300000, 300000);
        System.out.println("[" + DateTimeUtil.getFormattedDateTimeNow() + "] Auto-save timer started for: " + filename + " (interval: 5 minutes)");
    }
    
    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            System.out.println("[" + DateTimeUtil.getFormattedDateTimeNow() + "] Auto-save timer stopped for: " + filename);
        }
    }
    //console outputs used to check whether autosave is running in the background or not.
}