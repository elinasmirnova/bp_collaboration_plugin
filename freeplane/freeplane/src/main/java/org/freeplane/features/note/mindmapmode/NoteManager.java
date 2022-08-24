/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2008 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitry Polivaev
 *
 *  This file is created by Dimitry Polivaev in 2008.
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
package org.freeplane.features.note.mindmapmode;

import javax.swing.Icon;
import javax.swing.SwingUtilities;

import org.freeplane.core.util.HtmlUtils;
import org.freeplane.core.util.LogUtils;
import org.freeplane.core.util.TextUtils;
import org.freeplane.features.map.IMapSelectionListener;
import org.freeplane.features.map.INodeSelectionListener;
import org.freeplane.features.map.MapModel;
import org.freeplane.features.map.NodeModel;
import org.freeplane.features.mode.Controller;
import org.freeplane.features.note.NoteModel;
import org.freeplane.features.text.TextController;

final class NoteManager implements INodeSelectionListener, IMapSelectionListener {
	private boolean ignoreEditorUpdate;
	NodeModel node;
	/**
	 *
	 */
	final MNoteController noteController;

	public NoteManager(final MNoteController noteController) {
		this.noteController = noteController;
	}

	@Override
	public void onDeselect(final NodeModel node) {
		final NotePanel notePanel = noteController.getNotePanel();
		if (notePanel == null) {
			return;
		}
		notePanel.removeDocumentListener();
		saveNote(node);
        notePanel.stopEditing();
		this.node = null;
	}

	@Override
	public void onSelect(final NodeModel node) {
		this.node = node;
		updateEditor();
	}


	void updateEditor() {
		final NotePanel notePanel = noteController.getNotePanel();
		if (notePanel == null) {
			return;
		}
		if(node == null) {
			return;
		}
		final String note = this.node != null ? NoteModel.getNoteText(this.node) : null;
		TextController textController = TextController.getController();
        noteController.setDefaultStyle(node);
        notePanel.updateBaseUrl(node.getMap().getURL());
		if (note != null) {
			try {
				final Object transformedContent = textController.getTransformedObject(node, NoteModel.getNote(node), note);
				Icon icon = textController.getIcon(transformedContent);
				if(icon != null)
					notePanel.setViewedImage(icon);
				else if (transformedContent == note) {
					if (ignoreEditorUpdate) {
						return;
					}
					notePanel.removeDocumentListener();
					notePanel.setEditedContent(note);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							notePanel.installDocumentListener();
						}
					});
				}
				else
					notePanel.setViewedContent(transformedContent.toString());
			}
			catch (Throwable e) {
				LogUtils.warn(e.getMessage());
				notePanel.setViewedContent(TextUtils.format("MainView.errorUpdateText", note, e.getLocalizedMessage()));
			}
		} else {
			String noteContentType = noteController.getNoteContentType(node);
			if (TextController.CONTENT_TYPE_AUTO.equals(noteContentType)
					|| TextController.CONTENT_TYPE_HTML.equals(noteContentType))
					notePanel.setEditedContent("");
			else
				notePanel.setViewedContent("");
		}
	}

	@Override
	public void afterMapChange(MapModel oldMap, MapModel newMap) {
		if(newMap == null) {
			node = null;
			final NotePanel notePanel = noteController.getNotePanel();
			if(notePanel != null)
				notePanel.setViewedContent("");
		}
	}
	
   void saveNote(final NodeModel node) {
        if (this.node != node) {
            return;
        } 
        saveNote();
    }

    void saveNote() {
        if (node == null) {
            return;
        }
        final NotePanel notePanel = noteController.getNotePanel();
        if (notePanel != null) {
            notePanel.saveNote();
        }
    }
	

	NodeModel getNode() {
		return node;
	}

    void saveNote(String text) {
        boolean isHtml = HtmlUtils.isHtml(text);
        boolean editorContentEmpty = isHtml && HtmlUtils.isEmpty(text)
                || ! isHtml && text.trim().isEmpty();
        Controller.getCurrentModeController().getMapController().removeNodeSelectionListener(this);
        try {
            ignoreEditorUpdate = true;
            if (editorContentEmpty) {
                noteController.setNoteText(node, null);
            }
            else {
                final String oldText = noteController.getNoteText(node);
                if (null == oldText)
                    noteController.setNoteText(node, text);
                else if(isHtml){
                    final String oldTextWithoutHead = NotePanel.HEAD.matcher(oldText).replaceFirst("");
                    if (!oldTextWithoutHead.trim().equals(text.trim()))
                        noteController.setNoteText(node, text);
                }
                else
                    noteController.setNoteText(node, text);
            }
        }
        finally {
            ignoreEditorUpdate = false;
        }
        Controller.getCurrentModeController().getMapController().addNodeSelectionListener(this);
    }
}
