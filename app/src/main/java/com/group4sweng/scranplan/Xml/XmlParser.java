package com.group4sweng.scranplan.Xml;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlParser {

    // TODO - Add handlers for multiple repeated tags
    // TODO - Add handlers for optional attributes (starttime, endtime etc.)

    private Defaults defaults;
    final static String TAG = "XmlParser";

    public Map<String, Object> parse(InputStream in) throws XmlPullParserException, IOException {

        // Initialises parser to process nulls using provided InputStream and invokes readFeed method
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private Map<String, Object> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        Map<String, Object> data = new HashMap<>();
        DocumentInfo documentInfo = null;
        List<Slide> slides = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, null, "slideshow");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("documentinfo")) {
                documentInfo = readDocumentInfo(parser);
            } else if (name.equals("defaults")) {
                defaults = readDefaults(parser);
            } else if (name.equals("slide")) {
                slides.add(readSlide(parser));
            } else {
                skip(parser);
            }
        }
        data.put("documentInfo", documentInfo);
        data.put("defaults", defaults);
        data.put("slides", slides);
        return data;
    }

    private DocumentInfo readDocumentInfo(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "documentinfo");

        String author = null;
        String dateModified = null;
        Float version = null;
        Integer totalSlides = null;
        String comment = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "author":
                    author = readString(parser);
                    break;
                case "datemodified":
                    dateModified = readString(parser);
                    break;
                case "version":
                    version = readFloat(parser);
                    break;
                case "totalslides":
                    totalSlides = readInteger(parser);
                    break;
                case "comment":
                    comment = readString(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        return new DocumentInfo(author, dateModified, version, totalSlides, comment);
    }

    private Defaults readDefaults(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "defaults");

        String backgroundColor = null;
        String font = null;
        String fontBackground = null;
        Integer fontSize = null;
        String fontColor = null;
        String lineColor = null;
        String fillColor = null;
        Integer slideWidth = null;
        Integer slideHeight = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "backgroundcolor":
                    backgroundColor = readString(parser);
                    break;
                case "font":
                    font = readString(parser);
                    break;
                case "fontBackground":
                    fontBackground = readString(parser);
                    break;
                case "fontsize":
                    fontSize = readInteger(parser);
                    break;
                case "fontcolor":
                    fontColor = readString(parser);
                    break;
                case "linecolor":
                    lineColor = readString(parser);
                    break;
                case "fillcolor":
                    fillColor = readString(parser);
                    break;
                case "slidewidth":
                    slideWidth = readInteger(parser);
                    break;
                case "slideheight":
                    slideHeight = readInteger(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        return new Defaults(backgroundColor, font, fontBackground, fontSize, fontColor, lineColor, fillColor, slideWidth, slideHeight);
    }

    private Slide readSlide(XmlPullParser parser) throws  IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "slide");

        String id = parser.getAttributeValue(null, "id");
        Integer duration = Integer.valueOf(parser.getAttributeValue(null, "duration"));
        Text text = null;
        ArrayList<Line> lines = new ArrayList<>();
        ArrayList<Shape> shapes = new ArrayList<>();
        ArrayList<Triangle> triangles = new ArrayList<>();
        Audio audio = null;
        Audio audioLooping = null;
        Image image = null;
        Video video = null;
        Float timer = null;

        // TODO - parser trips up on tags without an end tag e.g. <image ... />. NEEDS FIX
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "text":
                    text = readText(parser);
                    break;
                case "line":
                    lines.add(readLine(parser));
                    break;
                case "shape":
                    shapes.add(readShape(parser));
                    break;
                case "triangle":
                    triangles.add(readTriangle(parser));
                    break;
                case "audio":
                    /* Can read in up to 2 audio files. (one looping, one non-looping) Takes priority from highest to lowest.
                       'audio' tag in the file hierarchy. */

                    //  Store a copy of the audio class from the xml parser.
                    final Audio tempAudio = readAudio(parser);

                    if(!tempAudio.loop && audio == null){ //Check if the audio is looping or not and if a file has already been stored.
                        audio = tempAudio; //Assign the audio file to info read in through the parser.
                    } else if(tempAudio.loop && audioLooping == null){
                        audioLooping = tempAudio;
                    }
                    break;
                case "image":
                    image = readImage(parser);
                    break;
                case "video":
                    video = readVideo(parser);
                    break;
                case "timer":
                    timer = readFloat(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        return new Slide(id, duration, text, lines, shapes, triangles, audio, audioLooping, image, video, timer);
    }

    // TODO - as b and i are elements might need loop check
    private Text readText(XmlPullParser parser) throws  IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "text");

        String font = defaults.font;
        String background = defaults.fontBackground;
        Integer fontSize = defaults.fontSize;
        String fontColor = defaults.fontColor;
        Integer fontWeight = 300;
        Float xPos = 0f;
        Float yPos = 0f;
        Float height = -1f;
        Float width = 1f;
        Integer startTime = 0;
        Integer endTime = 0;
        String b = "";
        String i = "";

        if (parser.getAttributeValue(null, "font") != null) {
            font = parser.getAttributeValue(null, "font");
        }
        if (parser.getAttributeValue(null, "background") != null) {
            background = parser.getAttributeValue(null, "background");
        }
        if (parser.getAttributeValue(null, "fontsize") != null) {
            fontSize = Integer.valueOf(parser.getAttributeValue(null, "fontsize"));
        }
        if (parser.getAttributeValue(null, "fontcolor") != null) {
            font = parser.getAttributeValue(null, "fontcolor");
        }
        if (parser.getAttributeValue(null, "fontweight") != null) {
            fontWeight = Integer.valueOf(parser.getAttributeValue(null, "fontweight"));
        }
        if (parser.getAttributeValue(null, "xpos") != null) {
            xPos = Float.valueOf(parser.getAttributeValue(null, "xpos"));
        }
        if (parser.getAttributeValue(null, "ypos") != null) {
            yPos = Float.valueOf(parser.getAttributeValue(null, "ypos"));
        }
        if (parser.getAttributeValue(null, "height") != null) {
            height = Float.valueOf(parser.getAttributeValue(null, "height"));
        }
        if (parser.getAttributeValue(null, "width") != null) {
            width = Float.valueOf(parser.getAttributeValue(null, "width"));
        }
        if (parser.getAttributeValue(null, "starttime") != null) {
            startTime = Integer.valueOf(parser.getAttributeValue(null, "starttime"));
        }
        if (parser.getAttributeValue(null, "endtime") != null) {
            endTime = Integer.valueOf(parser.getAttributeValue(null, "endtime"));
        }
        if (parser.getAttributeValue(null, "b") != null) {
            b = parser.getAttributeValue(null, "b");
        }
        if (parser.getAttributeValue(null, "i") != null) {
            i = parser.getAttributeValue(null, "i");
        }

        String text = readString(parser);
        parser.require(XmlPullParser.END_TAG, null, "text");

        return new Text(text, font, background, fontSize, fontColor, fontWeight, xPos, yPos, height, width,startTime, endTime, b, i);
    }

    private Line readLine(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "line");

        Float xStart = Float.valueOf(parser.getAttributeValue(null, "xstart"));
        Float yStart = Float.valueOf(parser.getAttributeValue(null, "ystart"));
        Float xEnd = Float.valueOf(parser.getAttributeValue(null, "xend"));
        Float yEnd = Float.valueOf(parser.getAttributeValue(null, "yend"));
        String lineColor = parser.getAttributeValue(null, "linecolor");
        Integer startTime = Integer.valueOf(parser.getAttributeValue(null, "starttime"));
        Integer endTime = Integer.valueOf(parser.getAttributeValue(null, "endtime"));

//        parser.require(XmlPullParser.END_TAG, null, "line");
        parser.nextTag();

        return new Line(xStart, yStart, xEnd, yEnd, lineColor, startTime, endTime);
    }

    // TODO - Put loop to check if reaches END_TAG or shading tag
    private Shape readShape(XmlPullParser parser) throws  IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, null, "shape");

        Shading shading = null;

        String type = parser.getAttributeValue(null, "type");
        Float xStart = Float.valueOf(parser.getAttributeValue(null, "xstart"));
        Float yStart = Float.valueOf(parser.getAttributeValue(null, "ystart"));
        Float width = Float.valueOf(parser.getAttributeValue(null, "width"));
        Float height = Float.valueOf(parser.getAttributeValue(null, "height"));
        String fillColor = parser.getAttributeValue(null, "fillcolor");
        Integer startTime = Integer.valueOf(parser.getAttributeValue(null, "starttime"));
        Integer endTime = Integer.valueOf(parser.getAttributeValue(null, "endtime"));
//        Shading shading = readShading(parser);

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            if (parser.getName().equals("shading")) {
                shading = readShading(parser);
            }
        }

        return new Shape(type, xStart, yStart, width, height, fillColor, startTime, endTime, shading);
    }

    private Triangle readTriangle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "triangle");

        Float xstart = Float.valueOf(parser.getAttributeValue(null, "xstart"));
        Float ystart = Float.valueOf(parser.getAttributeValue(null, "ystart"));
        Float width = Float.valueOf(parser.getAttributeValue(null, "width"));
        String fillColor = parser.getAttributeValue(null, "fillcolor");
        Integer startTime = Integer.valueOf(parser.getAttributeValue(null, "starttime"));
        Integer endTime = Integer.valueOf(parser.getAttributeValue(null, "endtime"));
        Shading shading = null;

        parser.nextTag();

        return new Triangle(xstart, ystart, width, fillColor, startTime, endTime, shading);
    }

    private Shading readShading(XmlPullParser parser) throws  IOException, XmlPullParserException {
        Float x1 = Float.valueOf(parser.getAttributeValue(null, "x1"));
        Float y1 = Float.valueOf(parser.getAttributeValue(null, "y1"));
        Float x2 = Float.valueOf(parser.getAttributeValue(null, "x2"));
        Float y2 = Float.valueOf(parser.getAttributeValue(null, "y2"));
        String color1 =  parser.getAttributeValue(null, "color1");
        String color2 = parser.getAttributeValue(null, "color2");
        Boolean cyclic = Boolean.valueOf(parser.getAttributeValue(null, "cyclic"));

        parser.nextTag();

        return new Shading(x1, y1, x2, y2, color1, color2, cyclic);
    }

    /** Read in the audio from the XML file.
     * @param parser - XML parser
     * @return - A new audio class object.
     * @throws IOException - Error reading input/output of XML file.
     * @throws XmlPullParserException - Generic XML parser exception.
     */
    private Audio readAudio(XmlPullParser parser) throws  IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "audio"); //Require an 'audio' start tag.

        String urlName = parser.getAttributeValue(null, "urlname"); //Get the URL location of the audio
        Integer startTime = Integer.valueOf(parser.getAttributeValue(null, "starttime")); //Return the start time of the audio.
        Boolean loop = Boolean.valueOf(parser.getAttributeValue(null, "loop")); //Find if the audio is looping or not. (boolean)

        parser.nextTag(); // Skips final tag checks. audio only has attributed values.

        return new Audio(urlName, startTime, loop);
    }


    private Image readImage(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "image");

        String urlName = parser.getAttributeValue(null, "urlname");
        urlName = urlName.replace("%26", "&");
        Float xStart = Float.valueOf(parser.getAttributeValue(null, "xstart"));
        Float yStart = Float.valueOf(parser.getAttributeValue(null, "ystart"));
        Float width = Float.valueOf(parser.getAttributeValue(null, "width"));
        Float height = Float.valueOf(parser.getAttributeValue(null, "height"));
        Integer startTime = Integer.valueOf(parser.getAttributeValue(null, "starttime"));
        Integer endTime = Integer.valueOf(parser.getAttributeValue(null, "endtime"));

        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, null, "image");

        return new Image(urlName, xStart, yStart, width, height, startTime, endTime);
    }

    private Video readVideo(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "video");

        String urlName = parser.getAttributeValue(null, "urlname");
        Integer startTime = Integer.valueOf(parser.getAttributeValue(null, "starttime"));
        Boolean loop = Boolean.valueOf(parser.getAttributeValue(null, "loop"));
        Float xStart = Float.valueOf(parser.getAttributeValue(null, "xstart"));
        Float yStart = Float.valueOf(parser.getAttributeValue(null, "yStart"));

        parser.require(XmlPullParser.END_TAG, null, "video");

        return new Video(urlName, startTime, loop, xStart, yStart);
    }

    private String readString(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        result = result.replace("\n", "").replace("\t", "");
        return result;
    }

    private Integer readInteger(XmlPullParser parser) throws IOException, XmlPullParserException {
        Integer result = 0;
        if (parser.next() == XmlPullParser.TEXT) {
            result = Integer.valueOf(parser.getText());
            parser.nextTag();
        }
        return result;
    }

    private Float readFloat(XmlPullParser parser) throws IOException, XmlPullParserException {
        Float result = 0f;
        if (parser.next() == XmlPullParser.TEXT) {
            result = Float.valueOf(parser.getText());
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {

        //Throws exception if the current event isn't a start tag
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }

        /* Consumes start tag and all events up to and including the matching end tag
           Keeps track of nesting depth to match correct end tag to start tag */
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    //TODO - worth moving these into their own class files/different folder/nesting them in a different class?

    public static class DocumentInfo {
        public final String author;
        public final String dateModified;
        public final Float version;
        public final Integer totalSlides;
        public final String comment;

        public DocumentInfo(String author, String dateModified, Float version,
                            Integer totalSlides, String comment) {
            this.author = author;
            this.dateModified = dateModified;
            this.version = version;
            this.totalSlides = totalSlides;
            this.comment = comment;
        }
    }

    public static class Defaults {
        public final String backgroundColor;
        public final String font;
        public final String fontBackground;
        public final Integer fontSize;
        public final String fontColor;
        public final String lineColor;
        public final String fillColor;
        public final Integer slideWidth;
        public final Integer slideHeight;

        public Defaults(String backgroundColor, String font, String fontBackground, Integer fontSize,
                        String fontColor, String lineColor, String fillColor, Integer slideWidth, Integer slideHeight) {
            this.backgroundColor = backgroundColor;
            this.font = font;
            this.fontBackground = fontBackground;
            this.fontSize = fontSize;
            this.fontColor = fontColor;
            this.lineColor = lineColor;
            this.fillColor = fillColor;
            this.slideWidth = slideWidth;
            this.slideHeight = slideHeight;
        }
    }

    public static class Slide {
        public String id;
        public Integer duration;
        public Text text;
        public ArrayList<Line> lines;
        public ArrayList<Shape> shapes;
        public ArrayList<Triangle> triangles;
        public Audio audio;
        public Audio audioLooping;
        public Image image;
        public Video video;
        public Float timer;

        public Slide() {}

        public Slide(String id, Integer duration, Text text, ArrayList<Line> lines, ArrayList<Shape> shapes,
                     ArrayList<Triangle> triangles, Audio audio, Audio audioLooping, Image image, Video video, Float timer) {
            this.id = id;
            this.duration = duration;
            this.text = text;
            this.lines = lines;
            this.shapes = shapes;
            this.triangles = triangles;
            this.audio = audio;
            this.audioLooping = audioLooping;
            this.image = image;
            this.video = video;
            this.timer = timer;
        }
    }

    public static class Text {
        public String text;
        public final String background;
        public final String font;
        public final Integer fontSize;
        public final String fontColor;
        public final Integer fontWeight;
        public final Float xPos;
        public final Float yPos;
        public final Float height;
        public final Float width;
        public final Integer startTime;
        public final Integer endTime;
        public final String b;
        public final String i;

        public Text(String text, Defaults defaults) {
            this.text = text;
            this.background = defaults.fontBackground;
            this.font = defaults.font;
            this.fontSize = defaults.fontSize;
            this.fontColor = defaults.fontColor;
            this.fontWeight = 300;
            this.xPos = 0f;
            this.yPos = 0f;
            this.height = -1f;
            this.width = -1f;
            this.startTime = 0;
            this.endTime = 0;
            this.b = "";
            this.i = "";
        }

        public Text(String text, String background, String font, Integer fontSize, String fontColor, Integer fontWeight, Float xPos,
                     Float yPos, Float height, Float width, Integer startTime, Integer endTime, String b, String i) {
            this.text = text;
            this.background = background;
            this.font = font;
            this.fontSize = fontSize;
            this.fontColor = fontColor;
            this.fontWeight = fontWeight;
            this.xPos = xPos;
            this.yPos = yPos;
            this.height = height;
            this.width = width;
            this.startTime = startTime;
            this.endTime = endTime;
            this.b = b;
            this.i = i;
        }
    }

    public static class Line {
        public final Float xStart;
        public final Float yStart;
        public final Float xEnd;
        public final Float yEnd;
        public final String lineColor;
        public final Integer startTime;
        public final Integer endTime;

        public Line(Float xStart, Float yStart, Float xEnd, Float yEnd,
                    String lineColor, Integer startTime, Integer endTime) {
            this.xStart = xStart;
            this.yStart = yStart;
            this.xEnd = xEnd;
            this.yEnd = yEnd;
            this.lineColor = lineColor;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    public static class Shape implements Serializable {
        public final String type; //TODO - the type for this is "'oval'|'rectangle'" so needs changing from String
        public final Float xStart;
        public final Float yStart;
        public final Float width;
        public final Float height;
        public final String fillColor;
        public final Integer startTime;
        public final Integer endTime;
        public final Shading shading;

        public Shape(String type, Float xStart, Float yStart, Float width,
                     Float height, String fillColor, Integer startTime,
                     Integer endTime, Shading shading) {
            this.type = type;
            this.xStart = xStart;
            this.yStart = yStart;
            this.width = width;
            this.height = height;
            this.fillColor = fillColor;
            this.startTime = startTime;
            this.endTime = endTime;
            this.shading = shading;
        }
    }

    public static class Triangle implements Serializable {
        public final Float centreX;
        public final Float centreY;
        public final Float width;
        public final String fillColor;
        public final Integer startTime;
        public final Integer endTime;
        public final Shading shading;

        public Triangle(Float centreX, Float centreY, Float width, String fillColor,
                        Integer startTime, Integer endTime, Shading shading) {
            this.centreX = centreX;
            this.centreY = centreY;
            this.width = width;
            this.fillColor = fillColor;
            this.startTime = startTime;
            this.endTime = endTime;
            this.shading = shading;
        }
    }

    public static class Shading {
        public final Float x1;
        public final Float y1;
        public final Float x2;
        public final Float y2;
        public final String color1;
        public final String color2;
        public final Boolean cyclic;

        public Shading(Float x1, Float y1, Float x2, Float y2,
                       String color1, String color2, Boolean cyclic) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.color1 = color1;
            this.color2 = color2;
            this.cyclic = cyclic;
        }
    }

    /** XML data for the audio to be played.
     *  Scranplan uses the audio for the timer module within the presentation. Once the timer finishes the non-looping audio file is played.
     *  an optional 'looping' audio file can also be played as a timer sound. IE the default is an Egg timer.
     */
    public static class Audio {
        public final String urlName; //Direct URL of the audio to be played.
        public final Integer startTime; //Position in time to start audio from (Default = 0).
        public final Boolean loop; //Should the audio player loop?

        public Audio(String urlName, Integer startTime, Boolean loop) {
            this.urlName = urlName;
            this.startTime = startTime;
            this.loop = loop;
        }
    }

    public static class Image {
        public final String urlName;
        public final Float xStart;
        public final Float yStart;
        public final Float width;
        public final Float height;
        public final Integer startTime;
        public final Integer endTime;

        public Image(String urlName, Float xStart, Float yStart, Float width,
                     Float height, Integer startTime, Integer endTime) {
            this.urlName = urlName;
            this.xStart = xStart;
            this.yStart = yStart;
            this.width = width;
            this.height = height;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    public static class Video {
        public final String urlName;
        public final Integer startTime;
        public final Boolean loop;
        public final Float xStart;
        public final Float yStart;

        public Video(String urlName, Integer startTime, Boolean loop,
                     Float xStart, Float yStart) {
            this.urlName = urlName;
            this.startTime = startTime;
            this.loop = loop;
            this.xStart = xStart;
            this.yStart = yStart;
        }
    }
}