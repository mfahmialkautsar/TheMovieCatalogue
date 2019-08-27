package ga.softogi.themoviecatalogue.widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class StackWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
//        String type = intent.getStringExtra("extra_type");
//        Log.e("EXTRAS", type);
//        if (type.equals("movie")) {
            return new StackRemoteViewsFactory(this.getApplicationContext());
//        } else if (type.equals("tv")) {
//            return new TvStackRemoteViewsFactory(this.getApplicationContext());
//        } else {
//            return null;
//        }
    }
}
