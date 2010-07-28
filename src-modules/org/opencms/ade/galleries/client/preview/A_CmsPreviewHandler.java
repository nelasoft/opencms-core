/*
 * File   : $Source: /alkacon/cvs/opencms/src-modules/org/opencms/ade/galleries/client/preview/Attic/A_CmsPreviewHandler.java,v $
 * Date   : $Date: 2010/07/19 07:45:28 $
 * Version: $Revision: 1.4 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2002 - 2009 Alkacon Software (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.ade.galleries.client.preview;

import org.opencms.ade.galleries.client.preview.ui.A_CmsPreviewDialog;
import org.opencms.ade.galleries.shared.CmsResourceInfoBean;
import org.opencms.ade.galleries.shared.I_CmsGalleryProviderConstants.GalleryMode;

import java.util.Map;

import com.google.gwt.user.client.Command;

/**
 * Preview dialog handler.<p>
 * 
 * Delegates the actions of the preview controller to the preview dialog.<p>
 * 
 * @param <T> the resource info bean type
 * 
 * @author Polina Smagina
 * @author Tobias Herrmann
 * 
 * @version $Revision: 1.4 $ 
 * 
 * @since 8.0.0
 */
public abstract class A_CmsPreviewHandler<T extends CmsResourceInfoBean> implements I_CmsPreviewHandler<T> {

    /** The resource preview instance. */
    protected I_CmsResourcePreview m_resourcePreview;

    /**
     * Constructor.<p>
     * 
     * @param resourcePreview the resource preview instance
     */
    public A_CmsPreviewHandler(I_CmsResourcePreview resourcePreview) {

        m_resourcePreview = resourcePreview;
    }

    /**
     * @see org.opencms.ade.galleries.client.preview.I_CmsPreviewHandler#closePreview()
     */
    public void closePreview() {

        if (getDialog().hasChanges()) {
            //TODO: localization
            getDialog().confirmSaveChanges("Do you want to save before leaving the preview?", new Command() {

                /**
                 * @see com.google.gwt.user.client.Command#execute()
                 */
                public void execute() {

                    if (getDialog().getGalleryMode().equals(GalleryMode.editor)) {
                        CmsPreviewUtil.enableEditorOk(false);
                    }
                    getDialog().removePreview();
                    m_resourcePreview.clear();
                }
            }, null);
            return;
        }
        if (getDialog().getGalleryMode() == GalleryMode.editor) {
            CmsPreviewUtil.enableEditorOk(false);
        }
        getDialog().removePreview();
        m_resourcePreview.clear();
    }

    /**
     *  Returns the controller.<p>
     *  
     * @return the controller
     */
    public abstract A_CmsPreviewController<T> getController();

    /**
     * Returns the dialog.<p>
     * 
     * @return the dialog
     */
    public abstract A_CmsPreviewDialog<T> getDialog();

    /**
     * @see org.opencms.ade.galleries.client.preview.I_CmsPropertiesHandler#saveProperties(java.util.Map)
     */
    public void saveProperties(Map<String, String> properties) {

        getController().saveProperties(properties);
    }

    /**
     * @see org.opencms.ade.galleries.client.preview.I_CmsPropertiesHandler#selectResource()
     */
    public void selectResource() {

        getController().setResource(getDialog().getGalleryMode());
    }

    /**
     * @see org.opencms.ade.galleries.client.preview.I_CmsPreviewHandler#setDataInEditor()
     */
    public boolean setDataInEditor() {

        if (getDialog().getGalleryMode() == GalleryMode.editor) {
            if (getDialog().hasChanges()) {
                //TODO: localization
                getDialog().confirmSaveChanges("Do you want to save before leaving the dialog?", new Command() {

                    /**
                     * @see com.google.gwt.user.client.Command#execute()
                     */
                    public void execute() {

                        CmsPreviewUtil.closeDialog();
                    }
                }, null);
                return false;
            } else {
                getController().setResource(getDialog().getGalleryMode());
                return true;
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * @see org.opencms.ade.galleries.client.preview.I_CmsPreviewHandler#showData(org.opencms.ade.galleries.shared.CmsResourceInfoBean)
     */
    public void showData(T resourceInfo) {

        // once the resource info is displayed, enable the OK button for editor mode
        if (getDialog().getGalleryMode().equals(GalleryMode.editor)) {
            CmsPreviewUtil.enableEditorOk(true);
        }
        getDialog().fillContent(resourceInfo);
    }
}