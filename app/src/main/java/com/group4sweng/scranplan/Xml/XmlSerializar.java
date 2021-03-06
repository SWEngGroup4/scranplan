package com.group4sweng.scranplan.Xml;

import android.util.Xml;

import com.group4sweng.scranplan.Xml.XmlParser.Defaults;
import com.group4sweng.scranplan.Xml.XmlParser.DocumentInfo;
import com.group4sweng.scranplan.Xml.XmlParser.Line;
import com.group4sweng.scranplan.Xml.XmlParser.Shading;
import com.group4sweng.scranplan.Xml.XmlParser.Shape;
import com.group4sweng.scranplan.Xml.XmlParser.Slide;
import com.group4sweng.scranplan.Xml.XmlParser.Triangle;

import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;
import java.util.ArrayList;

public class XmlSerializar {

    // Create compatible XML file with provided data
    public void compile(FileOutputStream fileOutputStream, DocumentInfo documentInfo, Defaults defaults, ArrayList<Slide> slides) {
        XmlSerializer xmlSerializar = Xml.newSerializer();

        try {
            // XML file settings
            xmlSerializar.setOutput(fileOutputStream, "UTF-8");
            xmlSerializar.startDocument("UTF-8", true);
            xmlSerializar.startTag("", "slideshow");

            // Write document info and defaults to file
            writeDocInfo(xmlSerializar, documentInfo);
            writeDefaults(xmlSerializar, defaults);

            // Write each slide as its own element to file
            for (Slide slide : slides)
                writeSlide(xmlSerializar, slide);

            // Close tags and finish document
            xmlSerializar.endTag("", "slideshow");
            xmlSerializar.endDocument();
            fileOutputStream.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Write document info into file
    private Boolean writeDocInfo(XmlSerializer xmlSerializar, DocumentInfo documentInfo) {
        try {
            xmlSerializar.startTag("", "documentinfo");

            xmlSerializar.startTag("", "author");
            xmlSerializar.text(documentInfo.author);
            xmlSerializar.endTag("", "author");

            xmlSerializar.startTag("", "datemodified");
            xmlSerializar.text(documentInfo.dateModified);
            xmlSerializar.endTag("", "datemodified");

            xmlSerializar.startTag("", "version");
            xmlSerializar.text(documentInfo.version.toString());
            xmlSerializar.endTag("", "version");

            xmlSerializar.startTag("", "totalSlides");
            xmlSerializar.text(documentInfo.totalSlides.toString());
            xmlSerializar.endTag("", "totalSlides");

            xmlSerializar.startTag("", "comment");
            xmlSerializar.text(documentInfo.comment);
            xmlSerializar.endTag("", "comment");

            xmlSerializar.endTag("", "documentinfo");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Write defaults into file
    private Boolean writeDefaults(XmlSerializer xmlSerializer, Defaults defaults) {
        try {
            xmlSerializer.startTag("", "defaults");

            xmlSerializer.startTag("", "backgroundcolor");
            xmlSerializer.text(defaults.backgroundColor);
            xmlSerializer.endTag("", "backgroundcolor");

            xmlSerializer.startTag("", "font");
            xmlSerializer.text(defaults.font);
            xmlSerializer.endTag("", "font");

            xmlSerializer.startTag("", "fontBackground");
            xmlSerializer.text(defaults.fontBackground);
            xmlSerializer.endTag("", "fontBackground");

            xmlSerializer.startTag("", "fontsize");
            xmlSerializer.text(defaults.fontSize.toString());
            xmlSerializer.endTag("", "fontsize");

            xmlSerializer.startTag("", "fontcolor");
            xmlSerializer.text(defaults.fontColor);
            xmlSerializer.endTag("", "fontcolor");

            xmlSerializer.startTag("", "linecolor");
            xmlSerializer.text(defaults.lineColor);
            xmlSerializer.endTag("", "linecolor");

            xmlSerializer.startTag("", "fillcolor");
            xmlSerializer.text(defaults.fillColor);
            xmlSerializer.endTag("", "fillcolor");

            xmlSerializer.startTag("", "slidewidth");
            xmlSerializer.text(defaults.slideWidth.toString());
            xmlSerializer.endTag("", "slidewidth");

            xmlSerializer.startTag("", "slideheight");
            xmlSerializer.text(defaults.slideHeight.toString());
            xmlSerializer.endTag("", "slideheight");

            xmlSerializer.endTag("", "defaults");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Write slide into file
    private Boolean writeSlide(XmlSerializer xmlSerializer, Slide slide) {
        try {
            xmlSerializer.startTag("", "slide");
            xmlSerializer.attribute("", "id", slide.id);
            xmlSerializer.attribute("", "duration", slide.duration.toString());

            // Write text if present
            if (slide.text != null) {
                xmlSerializer.startTag("", "text");
                xmlSerializer.attribute("", "font", slide.text.font);
                xmlSerializer.attribute("", "fontBackground", slide.text.background);
                xmlSerializer.attribute("", "fontsize",
                        slide.text.fontSize.toString());
                xmlSerializer.attribute("", "fontcolor", slide.text.fontColor);
                xmlSerializer.attribute("", "xpos", slide.text.xPos.toString());
                xmlSerializer.attribute("", "ypos", slide.text.yPos.toString());
                xmlSerializer.attribute("", "width", slide.text.width.toString());
                xmlSerializer.attribute("", "height",
                        slide.text.height.toString());
                xmlSerializer.attribute("", "starttime",
                        slide.text.startTime.toString());
                xmlSerializer.attribute("", "endtime",
                        slide.text.endTime.toString());
                xmlSerializer.text(slide.text.text);
                xmlSerializer.endTag("", "text");
            }

            // Write lines is present
            if (!slide.lines.isEmpty()) {
                for (Line line : slide.lines) {
                    xmlSerializer.startTag("", "line");
                    xmlSerializer.attribute("", "xstart", line.xStart.toString());
                    xmlSerializer.attribute("", "ystart", line.yStart.toString());
                    xmlSerializer.attribute("", "xend", line.xEnd.toString());
                    xmlSerializer.attribute("", "yend", line.yEnd.toString());
                    xmlSerializer.attribute("", "linecolor", line.lineColor);
                    xmlSerializer.attribute("", "starttime",
                            line.startTime.toString());
                    xmlSerializer.attribute("", "endtime",
                            line.endTime.toString());
                    xmlSerializer.endTag("", "line");
                }
            }

            // Write shapes if present
            if (!slide.shapes.isEmpty()) {
                for (Shape shape : slide.shapes) {
                    xmlSerializer.startTag("", "shape");
                    xmlSerializer.attribute("", "type", shape.type);
                    xmlSerializer.attribute("", "xstart", shape.xStart.toString());
                    xmlSerializer.attribute("", "ystart", shape.yStart.toString());
                    xmlSerializer.attribute("", "width", shape.width.toString());
                    xmlSerializer.attribute("", "height", shape.height.toString());
                    xmlSerializer.attribute("", "fillcolor", shape.fillColor);
                    xmlSerializer.attribute("", "starttime",
                            shape.startTime.toString());
                    xmlSerializer.attribute("", "endtime",
                            shape.endTime.toString());

                    if (shape.shading != null)
                        writeShading(xmlSerializer, shape.shading);

                    xmlSerializer.endTag("", "shape");
                }
            }

            // Write triangles if present
            if (!slide.triangles.isEmpty()) {
                for (Triangle triangle : slide.triangles) {
                    xmlSerializer.startTag("", "triangle");
                    xmlSerializer.attribute("", "xstart",
                            triangle.centreX.toString());
                    xmlSerializer.attribute("", "ystart",
                            triangle.centreY.toString());
                    xmlSerializer.attribute("", "width",
                            triangle.width.toString());
                    xmlSerializer.attribute("", "fillcolor", triangle.fillColor);
                    xmlSerializer.attribute("", "starttime",
                            triangle.startTime.toString());
                    xmlSerializer.attribute("", "endtime",
                            triangle.endTime.toString());

                    if (triangle.shading != null)
                        writeShading(xmlSerializer, triangle.shading);

                    xmlSerializer.endTag("", "triangle");
                }
            }

            // Write audio is present
            if (slide.audio != null) {
                xmlSerializer.startTag("", "audio");
                xmlSerializer.attribute("", "urlname", slide.audio.urlName);
                xmlSerializer.attribute("", "starttime",
                        slide.audio.startTime.toString());
                xmlSerializer.attribute("", "loop", slide.audio.loop.toString());
                xmlSerializer.endTag("", "audio");
            }

            // Write looping audio is present
            if (slide.audioLooping != null) {
                xmlSerializer.startTag("", "audio");
                xmlSerializer.attribute("", "urlname", slide.audioLooping.urlName);
                xmlSerializer.attribute("", "starttime",
                        slide.audioLooping.startTime.toString());
                xmlSerializer.attribute("", "loop", slide.audioLooping.loop.toString());
                xmlSerializer.endTag("", "audio");
            }

            // Write image if present
            if (slide.image != null) {
                xmlSerializer.startTag("", "image");
                xmlSerializer.attribute("", "urlname", slide.image.urlName);
                xmlSerializer.attribute("", "xstart",
                        slide.image.xStart.toString());
                xmlSerializer.attribute("", "ystart",
                        slide.image.yStart.toString());
                xmlSerializer.attribute("", "width",
                        slide.image.width.toString());
                xmlSerializer.attribute("", "height",
                        slide.image.height.toString());
                xmlSerializer.attribute("", "starttime",
                        slide.image.startTime.toString());
                xmlSerializer.attribute("", "endtime",
                        slide.image.endTime.toString());
                xmlSerializer.endTag("", "image");
            }

            // Write video if present
            if (slide.video != null) {
                xmlSerializer.startTag("", "video");
                xmlSerializer.attribute("", "urlname", slide.video.urlName);
                xmlSerializer.attribute("", "starttime",
                        slide.video.startTime.toString());
                xmlSerializer.attribute("", "loop", slide.video.loop.toString());
                xmlSerializer.attribute("", "xstart",
                        slide.video.xStart.toString());
                xmlSerializer.attribute("", "ystart",
                        slide.video.yStart.toString());
                xmlSerializer.attribute("", "width",
                        slide.video.width.toString());
                xmlSerializer.attribute("", "height",
                        slide.video.height.toString());
                xmlSerializer.endTag("", "video");
            }

            // Write timer if present
            if (slide.timer != null) {
                xmlSerializer.startTag("", "timer");
                xmlSerializer.text(String.valueOf(Math.round(slide.timer)));
                xmlSerializer.endTag("", "timer");
            }

            xmlSerializer.endTag("", "slide");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Method to write shading element to file
    private boolean writeShading (XmlSerializer xmlSerializer, Shading shading) {
        try {
            xmlSerializer.startTag("", "shading");
            xmlSerializer.attribute("", "x1",
                    shading.x1.toString());
            xmlSerializer.attribute("", "y1",
                    shading.y1.toString());
            xmlSerializer.attribute("", "color1",
                    shading.color1);
            xmlSerializer.attribute("", "x2",
                    shading.x2.toString());
            xmlSerializer.attribute("", "y2",
                    shading.y2.toString());
            xmlSerializer.attribute("", "color2",
                    shading.color2);
            xmlSerializer.attribute("", "cyclic",
                    shading.cyclic.toString());
            xmlSerializer.endTag("", "shading");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
