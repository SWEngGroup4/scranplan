package com.group4sweng.scranplan.Presentation;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class XmlParser {

    // TODO - Add handlers for multiple repeated tags
    // TODO - Add handlers for optional attributes (starttime, endtime etc.)

    private Defaults defaults;

    Map<String, Object> parse(InputStream in) throws XmlPullParserException, IOException {

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

        return new Defaults(backgroundColor, font, fontSize, fontColor, lineColor, fillColor, slideWidth, slideHeight);
    }

    private Slide readSlide(XmlPullParser parser) throws  IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "slide");

        String id = parser.getAttributeValue(null, "id");
        Integer duration = Integer.valueOf(parser.getAttributeValue(null, "duration"));
        Text text = null;
        Line line = null;
        Shape shape = null;
        Audio audio = null;
        Image image = null;
        Video video = null;
        List<Comment> comments = null;
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
                    line = readLine(parser);
                    break;
                case "shape":
                    shape = readShape(parser);
                    break;
                case "audio":
                    audio = readAudio(parser);
                    break;
                case "image":
                    image = readImage(parser);
                    break;
                case "video":
                    video = readVideo(parser);
                    break;
                case "comments":
                    comments = readComments(parser);
                    break;
                case "timer":
                    timer = readFloat(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        return new Slide(id, duration, text, line, shape, audio, image, video, comments, timer);
    }

    // TODO - as b and i are elements might need loop check
    private Text readText(XmlPullParser parser) throws  IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "text");

        String font = defaults.font;
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

        return new Text(text, font, fontSize, fontColor, fontWeight, xPos, yPos, height, width,startTime, endTime, b, i);
    }

    private Line readLine(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "line");

        Float xStart = Float.valueOf(parser.getAttributeValue(null, "xstart"));
        Float yStart = Float.valueOf(parser.getAttributeValue(null, "ystart"));
        Float xEnd = Float.valueOf(parser.getAttributeValue(null, "xend"));
        Float yEnd = Float.valueOf(parser.getAttributeValue(null, "yEnd"));
        String lineColor = parser.getAttributeValue(null, "linecolor");
        Integer startTime = Integer.valueOf(parser.getAttributeValue(null, "starttime"));
        Integer endTime = Integer.valueOf(parser.getAttributeValue(null, "endtime"));

        parser.require(XmlPullParser.END_TAG, null, "line");

        return new Line(xStart, yStart, xEnd, yEnd, lineColor, startTime, endTime);
    }

    // TODO - Put loop to check if reaches END_TAG or shading tag
    private Shape readShape(XmlPullParser parser) throws  IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "shape");

        String type = parser.getAttributeValue(null, "type");
        Float xStart = Float.valueOf(parser.getAttributeValue(null, "xstart"));
        Float yStart = Float.valueOf(parser.getAttributeValue(null, "ystart"));
        Float width = Float.valueOf(parser.getAttributeValue(null, "width"));
        Float height = Float.valueOf(parser.getAttributeValue(null, "height"));
        String fillColor = parser.getAttributeValue(null, "fillcolor");
        Integer startTime = Integer.valueOf(parser.getAttributeValue(null, "starttime"));
        Integer endTime = Integer.valueOf(parser.getAttributeValue(null, "endtime"));
        Shading shading = readShading(parser);

        parser.require(XmlPullParser.END_TAG, null, "shape");

        return new Shape(type, xStart, yStart, width, height, fillColor, startTime, endTime, shading);
    }

    private Shading readShading(XmlPullParser parser) throws  IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "shading");

        Integer x1 = Integer.valueOf(parser.getAttributeValue(null, "x1"));
        Integer y1 = Integer.valueOf(parser.getAttributeValue(null, "y1"));
        Integer x2 = Integer.valueOf(parser.getAttributeValue(null, "x2"));
        Integer y2 = Integer.valueOf(parser.getAttributeValue(null, "y2"));
        String color1 =  parser.getAttributeValue(null, "color1");
        String color2 = parser.getAttributeValue(null, "color2");
        Boolean cyclic = Boolean.valueOf(parser.getAttributeValue(null, "cyclic"));

        parser.require(XmlPullParser.END_TAG, null, "shading");

        return new Shading(x1, y1, x2, y2, color1, color2, cyclic);
    }

    private Audio readAudio(XmlPullParser parser) throws  IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "audio");

        String urlName = parser.getAttributeValue(null, "urlname");
        Integer startTime = Integer.valueOf(parser.getAttributeValue(null, "starttime"));
        Boolean loop = Boolean.valueOf(parser.getAttributeValue(null, "loop"));

        parser.require(XmlPullParser.END_TAG, null, "audio");

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

    private List<Comment> readComments(XmlPullParser parser) throws  IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "comments");

        List<Comment> comments = new ArrayList<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if ("comment".equals(name)) {
                comments.add(readComment(parser));
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, null, "comments");

        return comments;
    }

    private Comment readComment(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "comment");

        String userID = parser.getAttributeValue(null, "userID");
        parser.nextTag();
        Text text = readText(parser);
        parser.nextTag();

        parser.require(XmlPullParser.END_TAG, null, "comment");

        return new Comment(userID, text);
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
        final String author;
        final String dateModified;
        final Float version;
        final Integer totalSlides;
        final String comment;

        DocumentInfo(String author, String dateModified, Float version,
                     Integer totalSlides, String comment) {
            this.author = author;
            this.dateModified = dateModified;
            this.version = version;
            this.totalSlides = totalSlides;
            this.comment = comment;
        }
    }

    static class Defaults {
        final String backgroundColor;
        final String font;
        final Integer fontSize;
        final String fontColor;
        final String lineColor;
        final String fillColor;
        final Integer slideWidth;
        final Integer slideHeight;

        private Defaults(String backgroundColor, String font, Integer fontSize,
                         String fontColor, String lineColor, String fillColor,
                         Integer slideWidth, Integer slideHeight) {
            this.backgroundColor = backgroundColor;
            this.font = font;
            this.fontSize = fontSize;
            this.fontColor = fontColor;
            this.lineColor = lineColor;
            this.fillColor = fillColor;
            this.slideWidth = slideWidth;
            this.slideHeight = slideHeight;
        }
    }

    static class Slide {
        final String id;
        final Integer duration;
        final Text text;
        final Line line;
        final Shape shape;
        final Audio audio;
        final Image image;
        final Video video;
        final List<Comment> comments;
        final Float timer;

        private Slide(String id, Integer duration, Text text, Line line, Shape shape,
                      Audio audio, Image image, Video video, List<Comment> comments, Float timer) {
            this.id = id;
            this.duration = duration;
            this.text = text;
            this.line = line;
            this.shape = shape;
            this.audio = audio;
            this.image = image;
            this.video = video;
            this.comments = comments;
            this.timer = timer;
        }
    }

    static class Text {
        String text;
        final String font;
        final Integer fontSize;
        final String fontColor;
        final Integer fontWeight;
        final Float xPos;
        final Float yPos;
        final Float height;
        final Float width;
        final Integer startTime;
        final Integer endTime;
        final String b;
        final String i;

        Text(String text, Defaults defaults) {
            this.text = text;
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

        private Text(String text, String font, Integer fontSize, String fontColor, Integer fontWeight, Float xPos,
                     Float yPos, Float height, Float width, Integer startTime, Integer endTime, String b, String i) {
            this.text = text;
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
        final Float xStart;
        final Float yStart;
        final Float xEnd;
        final Float yEnd;
        final String lineColor;
        final Integer startTime;
        final Integer endTime;

        private Line(Float xStart, Float yStart, Float xEnd, Float yEnd,
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

    public static class Shape {
        final String type; //TODO - the type for this is "'oval'|'rectangle'" so needs changing from String
        final Float xStart;
        final Float yStart;
        final Float width;
        final Float height;
        final String fillColor;
        final Integer startTime;
        final Integer endTime;
        final Shading shading;

        private Shape(String type, Float xStart, Float yStart, Float width,
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

    public static class Shading {
        final Integer x1;
        final Integer y1;
        final Integer x2;
        final Integer y2;
        final String color1;
        final String color2;
        final Boolean cyclic;

        private Shading(Integer x1, Integer y1, Integer x2, Integer y2,
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

    public static class Audio {
        final String urlName;
        final Integer startTime;
        final Boolean loop;

        private Audio(String urlName, Integer startTime, Boolean loop) {
            this.urlName = urlName;
            this.startTime = startTime;
            this.loop = loop;
        }
    }

    public static class Image {
        final String urlName;
        final Float xStart;
        final Float yStart;
        final Float width;
        final Float height;
        final Integer startTime;
        final Integer endTime;

        private Image(String urlName, Float xStart, Float yStart, Float width,
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
        final String urlName;
        final Integer startTime;
        final Boolean loop;
        final Float xStart;
        final Float yStart;

        private Video(String urlName, Integer startTime, Boolean loop,
                      Float xStart, Float yStart) {
            this.urlName = urlName;
            this.startTime = startTime;
            this.loop = loop;
            this.xStart = xStart;
            this.yStart = yStart;
        }
    }

    public static class Comment {
        final String userID;
        final Text text;

        private Comment(String userID, Text text) {
            this.userID = userID;
            this.text = text;
        }
    }
}