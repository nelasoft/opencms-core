/*
 * File   : $Source: /alkacon/cvs/opencms/src/org/opencms/file/types/CmsResourceTypeJsp.java,v $
 * Date   : $Date: 2005/03/15 18:05:54 $
 * Version: $Revision: 1.11 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Mananagement System
 *
 * Copyright (C) 2002 - 2005 Alkacon Software (http://www.alkacon.com)
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

package org.opencms.file.types;

import org.opencms.db.CmsSecurityManager;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProperty;
import org.opencms.file.CmsResource;
import org.opencms.i18n.CmsEncoder;
import org.opencms.loader.CmsJspLoader;
import org.opencms.main.CmsException;
import org.opencms.main.I_CmsConstants;
import org.opencms.main.OpenCms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resource type descriptor for the type "jsp".<p>
 * 
 * Ensures that some required file properties are attached to new JSPs.<p>
 * 
 * The value for the encoding properies of a new JSP usually is the
 * system default encoding, but this can be overwritten by 
 * a configuration parameters set in <code>opencms-vfs.xml</code>.<p>
 *
 * @author Alexander Kandzior (a.kandzior@alkacon.com)
 * 
 * @version $Revision: 1.11 $
 */
public class CmsResourceTypeJsp extends A_CmsResourceType {

    /** The configuration parameter for "default JSP encoding". */
    public static final String C_CONFIGURATION_JSP_ENCODING = "default.encoding";

    /** The type id of this resource type. */
    public static final int C_RESOURCE_TYPE_ID = 8;

    /** The name of this resource type. */
    public static final String C_RESOURCE_TYPE_NAME = "jsp";

    /** The default encoding to use when creating new JSP pages. */
    private String m_defaultEncoding;

    /**
     * @see org.opencms.file.types.A_CmsResourceType#addConfigurationParameter(java.lang.String, java.lang.String)
     */
    public void addConfigurationParameter(String paramName, String paramValue) {
        
        super.addConfigurationParameter(paramName, paramValue);
        if (C_CONFIGURATION_JSP_ENCODING.equalsIgnoreCase(paramName)) {
            m_defaultEncoding = CmsEncoder.lookupEncoding(paramValue.trim(), OpenCms.getSystemInfo()
                .getDefaultEncoding());
        }
    }

    /**
     * @see org.opencms.file.types.I_CmsResourceType#createResource(org.opencms.file.CmsObject, org.opencms.db.CmsSecurityManager, java.lang.String, byte[], java.util.List)
     */
    public CmsResource createResource(
        CmsObject cms,
        CmsSecurityManager securityManager,
        String resourcename,
        byte[] content,
        List properties
    ) throws CmsException {
        
        List newProperties;       
        if (properties == null) {
            newProperties = new ArrayList();
        } else {
            newProperties = new ArrayList(properties);
        }
        newProperties.add(new CmsProperty(I_CmsConstants.C_PROPERTY_EXPORT, null, "false"));
        newProperties.add(new CmsProperty(I_CmsConstants.C_PROPERTY_CONTENT_ENCODING, null, m_defaultEncoding));
        newProperties.addAll(createPropertyObjects(cms));
        
        return super.createResource(cms, securityManager, resourcename, content, newProperties);
    }

    /**
     * @see org.opencms.file.types.A_CmsResourceType#getConfiguration()
     */
    public Map getConfiguration() {

        Map result = new HashMap();
        result.put(C_CONFIGURATION_JSP_ENCODING, m_defaultEncoding);
        Map additional = super.getConfiguration();
        if (additional != null) {
            result.putAll(additional);
        }
        return result;
    }

    /**
     * Returns the default encoding for JSP pages.<p>
     * 
     * @return the default encoding for JSP pages
     */
    public String getDefaultEncoding() {

        return m_defaultEncoding;
    }

    /**
     * @see org.opencms.file.types.I_CmsResourceType#getLoaderId()
     */
    public int getLoaderId() {

        return CmsJspLoader.C_RESOURCE_LOADER_ID;
    }

    /**
     * @see org.opencms.file.types.I_CmsResourceType#getTypeId()
     */
    public int getTypeId() {

        return C_RESOURCE_TYPE_ID;
    }

    /**
     * @see org.opencms.file.types.A_CmsResourceType#getTypeName()
     */
    public String getTypeName() {

        return C_RESOURCE_TYPE_NAME;
    }

    /**
     * @see org.opencms.file.types.A_CmsResourceType#initialize(org.opencms.file.CmsObject)
     */
    public void initialize(CmsObject cms) {

        super.initialize(cms);
        // ensure default content encoding is set
        if (m_defaultEncoding == null) {
            m_defaultEncoding = OpenCms.getSystemInfo().getDefaultEncoding();
        }
        m_defaultEncoding = m_defaultEncoding.intern();
    }
}