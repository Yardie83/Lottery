import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.security.Key;
import java.util.Random;

/**
 * Created by Hermann Grieder on 19.10.2016.
 * <p>
 * Draw Animation Class
 */
class DrawAnimation {

    private Timeline timeLine;

    DrawAnimation( HBox numbersBox ) {

        Random rand = new Random();
        timeLine = new Timeline();
        timeLine.setCycleCount( 1 );
        for ( int i = 0; i < 50; i++ ) {
            for ( int j = 0; j < numbersBox.getChildren().size(); j++ ) {
                Label label = (Label) numbersBox.getChildren().get( j );
                timeLine.getKeyFrames().addAll(
                        new KeyFrame( Duration.millis( i * 100 ),
                                new KeyValue( label.textProperty(), String.valueOf( rand.nextInt( 42 ) + 1 ) )
                        ),
                        new KeyFrame( Duration.millis( ( i + 1 ) * 100 ),
                                new KeyValue( label.textProperty(), String.valueOf( rand.nextInt( 42 ) + 1 ) )
                        ) );
            }
        }
    }

    Timeline getTimeLine() {
        return timeLine;
    }
}
