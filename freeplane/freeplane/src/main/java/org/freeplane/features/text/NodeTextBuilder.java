/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2008 Dimitry Polivaev
 *
 *  This file author is Dimitry Polivaev
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freeplane.features.text;

import java.io.IOException;

import org.freeplane.core.extension.IExtension;
import org.freeplane.core.io.IAttributeHandler;
import org.freeplane.core.io.IAttributeWriter;
import org.freeplane.core.io.IElementContentHandler;
import org.freeplane.core.io.IElementWriter;
import org.freeplane.core.io.IExtensionAttributeWriter;
import org.freeplane.core.io.IExtensionElementWriter;
import org.freeplane.core.io.ITreeWriter;
import org.freeplane.core.io.ReadManager;
import org.freeplane.core.io.WriteManager;
import org.freeplane.core.resources.TranslatedObject;
import org.freeplane.core.util.HtmlUtils;
import org.freeplane.core.util.LogUtils;
import org.freeplane.core.util.TypeReference;
import org.freeplane.features.format.IFormattedObject;
import org.freeplane.features.map.MapWriter;
import org.freeplane.features.map.NodeBuilder;
import org.freeplane.features.map.NodeModel;
import org.freeplane.features.map.NodeWriter;
import org.freeplane.features.styles.StyleFactory;
import org.freeplane.features.styles.StyleTranslatedObject;
import org.freeplane.features.styles.StyleString;
import org.freeplane.n3.nanoxml.XMLElement;

public class NodeTextBuilder implements IElementContentHandler, IElementWriter, IAttributeWriter, IExtensionElementWriter, IExtensionAttributeWriter {
	public static final String TEXT_ELEMENT = "text";
	public static final String XML_NODE_TEXT = "TEXT";
	public static final String XML_NODE_LOCALIZED_TEXT = "LOCALIZED_TEXT";
	public static final String XML_NODE_RICHCONTENT_TAG = "richcontent";
	public static final String XML_RICHCONTENT_TYPE_NODE = "NODE";
	public static final String XML_RICHCONTENT_TYPE_NOTE = "NOTE";
	public static final String XML_RICHCONTENT_TYPE_DETAILS = "DETAILS";
	public static final String XML_RICHCONTENT_TYPE_ATTRIBUTE = "TYPE";
    public static final String XML_RICHCONTENT_CONTENT_TYPE_ATTRIBUTE = "CONTENT-TYPE";
    public static final String XML_NODE_OBJECT = "OBJECT";
	private static final String XML_NODE_TEXT_SHORTENED = "TEXT_SHORTENED";
	
	public Object createElement(final Object parent, final String tag, final XMLElement attributes) {
		if (attributes == null) {
			return null;
		}
		final Object typeAttribute = attributes.getAttribute(NodeTextBuilder.XML_RICHCONTENT_TYPE_ATTRIBUTE, null);
		if (NodeTextBuilder.XML_RICHCONTENT_TYPE_NODE.equals(typeAttribute)
				 || NodeTextBuilder.XML_RICHCONTENT_TYPE_DETAILS.equals(typeAttribute)) {
			return parent;
		}
		return null;
	}


    @Override
    public boolean containsXml(XMLElement element) {
        return ContentSyntax.XML.matches(element.getAttribute(NodeTextBuilder.XML_RICHCONTENT_CONTENT_TYPE_ATTRIBUTE, ContentSyntax.XML.prefix));
    }

	public void endElement(final Object parent, final String tag, final Object obj, final XMLElement element,
	                       final String content) {
		assert tag.equals("richcontent");
		final String text;
		if(content != null)
			text = content.trim();
		else {
			XMLElement textElement = element.getFirstChildNamed(TEXT_ELEMENT);
			text = textElement != null ? textElement.getContent() : null;
		}
		final String type = element.getAttribute(NodeTextBuilder.XML_RICHCONTENT_TYPE_ATTRIBUTE, null);
		final NodeModel nodeModel = (NodeModel) obj;
		if (NodeTextBuilder.XML_RICHCONTENT_TYPE_NODE.equals(type)) {
			nodeModel.setXmlText(text);
		}
		else if (NodeTextBuilder.XML_RICHCONTENT_TYPE_DETAILS.equals(type)) {
			final boolean hidden = "true".equals(element.getAttribute("HIDDEN", "false"));
			final DetailModel details = new DetailModel(hidden);
			if(containsXml(element))
			    details.setXml(text);
			else
			    details.setText(text);
            final String contentType = element.getAttribute(
                    NodeTextBuilder.XML_RICHCONTENT_CONTENT_TYPE_ATTRIBUTE, 
                    ContentSyntax.XML.prefix);
            details.setContentType(ContentSyntax.specificType(contentType));
			nodeModel.addExtension(details);
		}
	}

	private void registerAttributeHandlers(final ReadManager reader) {
		reader.addAttributeHandler(NodeBuilder.XML_NODE, NodeTextBuilder.XML_NODE_TEXT, new IAttributeHandler() {
			public void setAttribute(final Object userObject, final String value) {
				final NodeModel node = ((NodeModel) userObject);
				final Object nodeContent = node.getUserObject();
				if(nodeContent == null || nodeContent.equals("")){
					node.setText(value);
				}
			}
		});
		reader.addAttributeHandler(NodeBuilder.XML_NODE, NodeTextBuilder.XML_NODE_OBJECT, new IAttributeHandler() {
			public void setAttribute(final Object userObject, final String value) {
				final NodeModel node = ((NodeModel) userObject);
				final Object newInstance = TypeReference.create(value);
				// work around for old maps :
				// actually we do not need IFormattedObject as user objects
				// because formatting is saved as an extra attribute
				if(newInstance instanceof IFormattedObject)
					node.setUserObject(((IFormattedObject) newInstance).getObject());
				else
					node.setUserObject(newInstance);
			}
		});
		IAttributeHandler textShortenedHandler = new IAttributeHandler() {
			public void setAttribute(final Object userObject, final String value) {
				final NodeModel node = ((NodeModel) userObject);
				try {
					if(Boolean.valueOf(value)){
						node.addExtension(new ShortenedTextModel());
					}
				}
				catch (Exception e) {
					LogUtils.warn(e);
				}
			}
		};
		reader.addAttributeHandler(NodeBuilder.XML_NODE, NodeTextBuilder.XML_NODE_TEXT_SHORTENED, textShortenedHandler);
		reader.addAttributeHandler(NodeBuilder.XML_STYLENODE, NodeTextBuilder.XML_NODE_TEXT_SHORTENED, textShortenedHandler);
		
		reader.addAttributeHandler(NodeBuilder.XML_STYLENODE, NodeTextBuilder.XML_NODE_TEXT, new IAttributeHandler() {
			public void setAttribute(final Object userObject, final String value) {
				final NodeModel node = ((NodeModel) userObject);
				node.setUserObject(StyleFactory.create(value));
			}
		});
		reader.addAttributeHandler(NodeBuilder.XML_NODE, NodeTextBuilder.XML_NODE_LOCALIZED_TEXT, new IAttributeHandler() {
			public void setAttribute(final Object userObject, final String value) {
				final NodeModel node = ((NodeModel) userObject);
				node.setUserObject(StyleFactory.create(TranslatedObject.format(value)));
			}
		});
		reader.addAttributeHandler(NodeBuilder.XML_STYLENODE, NodeTextBuilder.XML_NODE_LOCALIZED_TEXT, new IAttributeHandler() {
			public void setAttribute(final Object userObject, final String value) {
				final NodeModel node = ((NodeModel) userObject);
				node.setUserObject(StyleFactory.create(TranslatedObject.format(value)));
			}
		});
	}

	/**
	 * @param writeManager
	 */
	public void registerBy(final ReadManager reader, final WriteManager writeManager) {
		registerAttributeHandlers(reader);
		reader.addElementHandler("richcontent", this);
		writeManager.addElementWriter(NodeBuilder.XML_NODE, this);
		writeManager.addElementWriter(NodeBuilder.XML_STYLENODE, this);
		writeManager.addAttributeWriter(NodeBuilder.XML_NODE, this);
		writeManager.addAttributeWriter(NodeBuilder.XML_STYLENODE, this);
	}

	private static class TransformedXMLExtension extends RichTextModel implements IExtension {
		public TransformedXMLExtension(String contentType, String original, String html) {
            super(contentType, original.equals(html) ? null : HtmlUtils.htmlToPlain(original), HtmlUtils.toXhtml(html));
        }
	}
	
	public void writeAttributes(final ITreeWriter writer, final Object userObject, final String tag) {
		if(! NodeWriter.shouldWriteSharedContent(writer))
			return;
		final NodeModel node = (NodeModel) userObject;
		final Object data = node.getUserObject();
		if(data == null)
			return;
		final Class<? extends Object> dataClass = data.getClass();
		if (dataClass.equals(StyleTranslatedObject.class)) {
			writer.addAttribute(NodeTextBuilder.XML_NODE_LOCALIZED_TEXT, ((StyleTranslatedObject) data).getObject().toString());
			return;
		}
		if (dataClass.equals(TranslatedObject.class)) {
			writer.addAttribute(NodeTextBuilder.XML_NODE_LOCALIZED_TEXT, ((TranslatedObject) data).getObject().toString());
			return;
		}
		final boolean forceFormatting = Boolean.TRUE.equals(writer.getHint(MapWriter.WriterHint.FORCE_FORMATTING));
		if (forceFormatting) {
			TextController textController = TextController.getController();
            final Object transformed = TextController.getController().getTransformedObjectNoFormattingNoThrow(node, node, data);
            final String text;
            if(!transformed.equals(data)) {
                String transformedHtml = HtmlUtils.objectToHtml(transformed);
                text = HtmlUtils.toXhtml(transformedHtml);
            }
            else
                text = data.toString();
			if (HtmlUtils.isHtml(text)) {
			    String original = data.toString();
				node.addExtension(new TransformedXMLExtension(textController.getNodeFormat(node), original, text));
			} else {
				writer.addAttribute(NodeTextBuilder.XML_NODE_TEXT, text.replace('\0', ' '));
			}
		}
		else{
			final String text =  data.toString();
			if (node.getXmlText() == null) {
				writer.addAttribute(NodeTextBuilder.XML_NODE_TEXT, text.replace('\0', ' '));
			}
			if(! (data instanceof String || data instanceof StyleString)){
				writer.addAttribute(XML_NODE_OBJECT, TypeReference.toSpec(data));
			}
		}
	}

	public void writeContent(final ITreeWriter writer, final Object object, final String tag) throws IOException {
		if(! NodeWriter.shouldWriteSharedContent(writer))
			return;
		final NodeModel node = (NodeModel) object;
		final TransformedXMLExtension transformedXML = node.getExtension(TransformedXMLExtension.class);
		if (transformedXML != null || node.getXmlText() != null) {
			final XMLElement element = new XMLElement();
			element.setName(NodeTextBuilder.XML_NODE_RICHCONTENT_TAG);
			element.setAttribute(NodeTextBuilder.XML_RICHCONTENT_TYPE_ATTRIBUTE, NodeTextBuilder.XML_RICHCONTENT_TYPE_NODE);
			final String xmlText;
			if (transformedXML != null){
				xmlText = transformedXML.getXml();
	            if(transformedXML.getText() != null) {
	                element.setAttribute("FORMAT", transformedXML.getContentType());
	                XMLElement textElement = element.createElement(TEXT_ELEMENT);
	                textElement.setContent(transformedXML.getText());
	                element.addChild(textElement);
	            }
				node.removeExtension(transformedXML);
			}
			else
				xmlText = node.getXmlText();
			final String content = xmlText.replace('\0', ' ');
			writer.addElement('\n' + content + '\n', element);
		}
	}
	/*
	 * (non-Javadoc)
	 * @see freeplane.io.INodeWriter#saveContent(freeplane.io.ITreeWriter,
	 * java.lang.Object, java.lang.String)
	 */
	public void writeContent(final ITreeWriter writer, final Object node, final IExtension extension) throws IOException {
		DetailModel details = (DetailModel) extension;
		final XMLElement element = new XMLElement();
		element.setName(NodeTextBuilder.XML_NODE_RICHCONTENT_TAG);
		
		String transformedXhtml = "";
		final boolean forceFormatting = Boolean.TRUE.equals(writer.getHint(MapWriter.WriterHint.FORCE_FORMATTING));
		if (forceFormatting) {
			String data = details.getText();
			if(data != null) {
				final Object transformed = TextController.getController().getTransformedObjectNoFormattingNoThrow((NodeModel) node, details, data);
				if(!transformed.equals(data)) {
					String transformedHtml = HtmlUtils.objectToHtml(transformed);
					transformedXhtml = HtmlUtils.toXhtml(transformedHtml);
				}
			}
		}
		
        boolean containsXml = transformedXhtml.isEmpty() && details.getXml() != null;
        String contentType = details.getContentType();
        ContentSyntax contentSyntax = containsXml ? ContentSyntax.XML : ContentSyntax.PLAIN;
        element.setAttribute(NodeTextBuilder.XML_RICHCONTENT_CONTENT_TYPE_ATTRIBUTE, contentSyntax.with(contentType));
        element.setAttribute(NodeTextBuilder.XML_RICHCONTENT_TYPE_ATTRIBUTE, NodeTextBuilder.XML_RICHCONTENT_TYPE_DETAILS);
        if(details.isHidden()){
            element.setAttribute("HIDDEN", "true");
        }
        if (containsXml) {
        		final String content = details.getXml().replace('\0', ' ');
        		writer.addElement(content, element);
        }
        else {
            String text = details.getText();
            if(text != null) {
            	XMLElement textElement = element.createElement(TEXT_ELEMENT);
            	textElement.setContent(HtmlUtils.htmlToPlain(text));
            	element.addChild(textElement);
            }
            writer.addElement(transformedXhtml, element);
        }
		return;
	}

	public void writeAttributes(ITreeWriter writer, Object userObject, IExtension extension) {
		writer.addAttribute(XML_NODE_TEXT_SHORTENED, Boolean.TRUE.toString());
    }
}
