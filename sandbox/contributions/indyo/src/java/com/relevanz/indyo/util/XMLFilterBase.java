begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*--   Copyright (C) 2000 Brett McLaughlin& Jason Hunter.  All rights reserved.   Redistribution and use in source and binary forms, with or without  modification, are permitted provided that the following conditions  are met:   1. Redistributions of source code must retain the above copyright     notice, this list of conditions, and the following disclaimer.   2. Redistributions in binary form must reproduce the above copyright     notice, this list of conditions, and the disclaimer that follows     these conditions in the documentation and/or other materials     provided with the distribution.   3. The name "JDOM" must not be used to endorse or promote products     derived from this software without prior written permission.  For     written permission, please contact license@jdom.org.   4. Products derived from this software may not be called "JDOM", nor     may "JDOM" appear in their name, without prior written permission     from the JDOM Project Management (pm@jdom.org).   In addition, we request (but do not require) that you include in the  end-user documentation provided with the redistribution and/or in the  software itself an acknowledgement equivalent to the following:      "This product includes software developed by the       JDOM Project (http://www.jdom.org/)."  Alternatively, the acknowledgment may be graphical using the logos  available at http://www.jdom.org/images/logos.   THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  DISCLAIMED.  IN NO EVENT SHALL THE JDOM AUTHORS OR THE PROJECT  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  SUCH DAMAGE.   This software consists of voluntary contributions made by many  individuals on behalf of the JDOM Project and was originally  created by Brett McLaughlin<brett@jdom.org> and  Jason Hunter<jhunter@jdom.org>.  For more information on the  JDOM Project, please see<http://www.jdom.org/>.   */
end_comment

begin_package
DECL|package|com.relevanz.indyo.util
package|package
name|com
operator|.
name|relevanz
operator|.
name|indyo
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|Attributes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXNotRecognizedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXNotSupportedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|XMLReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|ext
operator|.
name|LexicalHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|XMLFilterImpl
import|;
end_import

begin_comment
comment|/**  * Adds convenience methods to base SAX2 Filter implementation.  *  *<i>Code and comments adapted from XMLWriter-0.2, written  * by David Megginson and released into the public domain,  * without warranty.</i>  *  *<p>The convenience methods are provided so that clients do not have to  * create empty attribute lists or provide empty strings as parameters;  * for example, the method invocation</p>  *  *<pre>  * w.startElement("foo");  *</pre>  *  *<p>is equivalent to the regular SAX2 ContentHandler method</p>  *  *<pre>  * w.startElement("", "foo", "", new AttributesImpl());  *</pre>  *  *<p>Except that it is more efficient because it does not allocate  * a new empty attribute list each time.</p>  *  *<p>In fact, there is an even simpler convenience method,  *<var>dataElement</var>, designed for writing elements that  * contain only character data.</p>  *  *<pre>  * w.dataElement("greeting", "Hello, world!");  *</pre>  *  *<p>is equivalent to</p>  *  *<pre>  * w.startElement("greeting");  * w.characters("Hello, world!");  * w.endElement("greeting");  *</pre>  *  * @see org.xml.sax.helpers.XMLFilterImpl  */
end_comment

begin_class
DECL|class|XMLFilterBase
class|class
name|XMLFilterBase
extends|extends
name|XMLFilterImpl
block|{
comment|////////////////////////////////////////////////////////////////////
comment|// Constructors.
comment|////////////////////////////////////////////////////////////////////
comment|/**      * Construct an XML filter with no parent.      *      *<p>This filter will have no parent: you must assign a parent      * before you start a parse or do any configuration with      * setFeature or setProperty.</p>      *      * @see org.xml.sax.XMLReader#setFeature      * @see org.xml.sax.XMLReader#setProperty      */
DECL|method|XMLFilterBase
specifier|public
name|XMLFilterBase
parameter_list|()
block|{     }
comment|/**      * Create an XML filter with the specified parent.      *      *<p>Use the XMLReader provided as the source of events.</p>      *      * @param xmlreader The parent in the filter chain.      */
DECL|method|XMLFilterBase
specifier|public
name|XMLFilterBase
parameter_list|(
name|XMLReader
name|parent
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
comment|////////////////////////////////////////////////////////////////////
comment|// Convenience methods.
comment|////////////////////////////////////////////////////////////////////
comment|/**      * Start a new element without a qname or attributes.      *      *<p>This method will provide a default empty attribute      * list and an empty string for the qualified name.      * It invokes {@link      * #startElement(String, String, String, Attributes)}      * directly.</p>      *      * @param uri The element's Namespace URI.      * @param localName The element's local name.      * @exception org.xml.sax.SAXException If a filter      *            further down the chain raises an exception.      * @see org.xml.sax.ContentHandler#startElement      */
DECL|method|startElement
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|)
throws|throws
name|SAXException
block|{
name|startElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
literal|""
argument_list|,
name|EMPTY_ATTS
argument_list|)
expr_stmt|;
block|}
comment|/**      * Start a new element without a qname, attributes or a Namespace URI.      *      *<p>This method will provide an empty string for the      * Namespace URI, and empty string for the qualified name,      * and a default empty attribute list. It invokes      * #startElement(String, String, String, Attributes)}      * directly.</p>      *      * @param localName The element's local name.      * @exception org.xml.sax.SAXException If a filter      *            further down the chain raises an exception.      * @see org.xml.sax.ContentHandler#startElement      */
DECL|method|startElement
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|localName
parameter_list|)
throws|throws
name|SAXException
block|{
name|startElement
argument_list|(
literal|""
argument_list|,
name|localName
argument_list|,
literal|""
argument_list|,
name|EMPTY_ATTS
argument_list|)
expr_stmt|;
block|}
comment|/**      * End an element without a qname.      *      *<p>This method will supply an empty string for the qName.      * It invokes {@link #endElement(String, String, String)}      * directly.</p>      *      * @param uri The element's Namespace URI.      * @param localName The element's local name.      * @exception org.xml.sax.SAXException If a filter      *            further down the chain raises an exception.      * @see org.xml.sax.ContentHandler#endElement      */
DECL|method|endElement
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|)
throws|throws
name|SAXException
block|{
name|endElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
comment|/**      * End an element without a Namespace URI or qname.      *      *<p>This method will supply an empty string for the qName      * and an empty string for the Namespace URI.      * It invokes {@link #endElement(String, String, String)}      * directly.</p>      *      * @param localName The element's local name.      * @exception org.xml.sax.SAXException If a filter      *            further down the chain raises an exception.      * @see org.xml.sax.ContentHandler#endElement      */
DECL|method|endElement
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|localName
parameter_list|)
throws|throws
name|SAXException
block|{
name|endElement
argument_list|(
literal|""
argument_list|,
name|localName
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
comment|/**      * Add an empty element.      *      * Both a {@link #startElement startElement} and an      * {@link #endElement endElement} event will be passed on down      * the filter chain.      *      * @param uri The element's Namespace URI, or the empty string      *        if the element has no Namespace or if Namespace      *        processing is not being performed.      * @param localName The element's local name (without prefix).  This      *        parameter must be provided.      * @param qName The element's qualified name (with prefix), or      *        the empty string if none is available.  This parameter      *        is strictly advisory: the writer may or may not use      *        the prefix attached.      * @param atts The element's attribute list.      * @exception org.xml.sax.SAXException If a filter      *            further down the chain raises an exception.      * @see org.xml.sax.ContentHandler#startElement      * @see org.xml.sax.ContentHandler#endElement      */
DECL|method|emptyElement
specifier|public
name|void
name|emptyElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
name|startElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|,
name|atts
argument_list|)
expr_stmt|;
name|endElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|)
expr_stmt|;
block|}
comment|/**       * Add an empty element without a qname or attributes.       *       *<p>This method will supply an empty string for the qname       * and an empty attribute list.  It invokes       * {@link #emptyElement(String, String, String, Attributes)}       * directly.</p>       *       * @param uri The element's Namespace URI.       * @param localName The element's local name.       * @exception org.xml.sax.SAXException If a filter       *            further down the chain raises an exception.       * @see #emptyElement(String, String, String, Attributes)       */
DECL|method|emptyElement
specifier|public
name|void
name|emptyElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|)
throws|throws
name|SAXException
block|{
name|emptyElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
literal|""
argument_list|,
name|EMPTY_ATTS
argument_list|)
expr_stmt|;
block|}
comment|/**      * Add an empty element without a Namespace URI, qname or attributes.      *      *<p>This method will supply an empty string for the qname,      * and empty string for the Namespace URI, and an empty      * attribute list.  It invokes      * {@link #emptyElement(String, String, String, Attributes)}      * directly.</p>      *      * @param localName The element's local name.      * @exception org.xml.sax.SAXException If a filter      *            further down the chain raises an exception.       * @see #emptyElement(String, String, String, Attributes)      */
DECL|method|emptyElement
specifier|public
name|void
name|emptyElement
parameter_list|(
name|String
name|localName
parameter_list|)
throws|throws
name|SAXException
block|{
name|emptyElement
argument_list|(
literal|""
argument_list|,
name|localName
argument_list|,
literal|""
argument_list|,
name|EMPTY_ATTS
argument_list|)
expr_stmt|;
block|}
comment|/**      * Add an element with character data content.      *      *<p>This is a convenience method to add a complete element      * with character data content, including the start tag      * and end tag.</p>      *      *<p>This method invokes      * {@link @see org.xml.sax.ContentHandler#startElement},      * followed by      * {@link #characters(String)}, followed by      * {@link @see org.xml.sax.ContentHandler#endElement}.</p>      *      * @param uri The element's Namespace URI.      * @param localName The element's local name.      * @param qName The element's default qualified name.      * @param atts The element's attributes.      * @param content The character data content.      * @exception org.xml.sax.SAXException If a filter      *            further down the chain raises an exception.      * @see org.xml.sax.ContentHandler#startElement      * @see #characters(String)      * @see org.xml.sax.ContentHandler#endElement      */
DECL|method|dataElement
specifier|public
name|void
name|dataElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|atts
parameter_list|,
name|String
name|content
parameter_list|)
throws|throws
name|SAXException
block|{
name|startElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|,
name|atts
argument_list|)
expr_stmt|;
name|characters
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|endElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|)
expr_stmt|;
block|}
comment|/**      * Add an element with character data content but no attributes.      *      *<p>This is a convenience method to add a complete element      * with character data content, including the start tag      * and end tag.  This method provides an empty string      * for the qname and an empty attribute list.</p>      *      *<p>This method invokes      * {@link @see org.xml.sax.ContentHandler#startElement},      * followed by      * {@link #characters(String)}, followed by      * {@link @see org.xml.sax.ContentHandler#endElement}.</p>      *      * @param uri The element's Namespace URI.      * @param localName The element's local name.      * @param content The character data content.      * @exception org.xml.sax.SAXException If a filter      *            further down the chain raises an exception.      * @see org.xml.sax.ContentHandler#startElement      * @see #characters(String)      * @see org.xml.sax.ContentHandler#endElement      */
DECL|method|dataElement
specifier|public
name|void
name|dataElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|content
parameter_list|)
throws|throws
name|SAXException
block|{
name|dataElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
literal|""
argument_list|,
name|EMPTY_ATTS
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
comment|/**      * Add an element with character data content but no attributes or      * Namespace URI.      *      *<p>This is a convenience method to add a complete element      * with character data content, including the start tag      * and end tag.  The method provides an empty string for the      * Namespace URI, and empty string for the qualified name,      * and an empty attribute list.</p>      *      *<p>This method invokes      * {@link @see org.xml.sax.ContentHandler#startElement},      * followed by      * {@link #characters(String)}, followed by      * {@link @see org.xml.sax.ContentHandler#endElement}.</p>      *      * @param localName The element's local name.      * @param content The character data content.      * @exception org.xml.sax.SAXException If a filter      *            further down the chain raises an exception.      * @see org.xml.sax.ContentHandler#startElement      * @see #characters(String)      * @see org.xml.sax.ContentHandler#endElement      */
DECL|method|dataElement
specifier|public
name|void
name|dataElement
parameter_list|(
name|String
name|localName
parameter_list|,
name|String
name|content
parameter_list|)
throws|throws
name|SAXException
block|{
name|dataElement
argument_list|(
literal|""
argument_list|,
name|localName
argument_list|,
literal|""
argument_list|,
name|EMPTY_ATTS
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
comment|/**      * Add a string of character data, with XML escaping.      *      *<p>This is a convenience method that takes an XML      * String, converts it to a character array, then invokes      * {@link @see org.xml.sax.ContentHandler#characters}.</p>      *      * @param data The character data.      * @exception org.xml.sax.SAXException If a filter      *            further down the chain raises an exception.      * @see @see org.xml.sax.ContentHandler#characters      */
DECL|method|characters
specifier|public
name|void
name|characters
parameter_list|(
name|String
name|data
parameter_list|)
throws|throws
name|SAXException
block|{
name|char
name|ch
index|[]
init|=
name|data
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|characters
argument_list|(
name|ch
argument_list|,
literal|0
argument_list|,
name|ch
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|////////////////////////////////////////////////////////////////////
comment|// Constants.
comment|////////////////////////////////////////////////////////////////////
DECL|field|EMPTY_ATTS
specifier|protected
specifier|static
specifier|final
name|Attributes
name|EMPTY_ATTS
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
block|}
end_class

begin_comment
comment|// end of XMLFilterBase.java
end_comment

end_unit

