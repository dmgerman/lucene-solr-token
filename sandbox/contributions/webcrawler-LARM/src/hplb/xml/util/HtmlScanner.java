begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * $Id$  *  * Copyright 1997 Hewlett-Packard Company  *  * This file may be copied, modified and distributed only in  * accordance with the terms of the limited licence contained  * in the accompanying file LICENSE.TXT.  */
end_comment

begin_package
DECL|package|hplb.xml.util
package|package
name|hplb
operator|.
name|xml
operator|.
name|util
package|;
end_package

begin_import
import|import
name|hplb
operator|.
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|HandlerBase
import|;
end_import

begin_import
import|import
name|hplb
operator|.
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|AttributeMap
import|;
end_import

begin_import
import|import
name|hplb
operator|.
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|XmlException
import|;
end_import

begin_import
import|import
name|hplb
operator|.
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|ErrorHandler
import|;
end_import

begin_import
import|import
name|hplb
operator|.
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|EntityHandler
import|;
end_import

begin_import
import|import
name|hplb
operator|.
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|DocumentHandler
import|;
end_import

begin_import
import|import
name|hplb
operator|.
name|xml
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * The HtmlScanner parses an HTML document for elements containing links.  * For each link found it will invoke a client-provided callback method.  * It knows about most HTML4.0 links and also knows about the&lt;base&gt;.  *  *<p>For an example use see UrlScanner.  *  * @see     HtmlObserver  * @see     UrlScanner  * @author  Anders Kristensen  */
end_comment

begin_class
DECL|class|HtmlScanner
specifier|public
class|class
name|HtmlScanner
extends|extends
name|HandlerBase
block|{
DECL|field|observer
name|HtmlObserver
name|observer
decl_stmt|;
DECL|field|contextURL
name|URL
name|contextURL
decl_stmt|;
DECL|field|data
name|Object
name|data
decl_stmt|;
DECL|field|tok
name|Tokenizer
name|tok
decl_stmt|;
DECL|field|in
name|Reader
name|in
decl_stmt|;
comment|/**      * Parse the input on the specified stream as if it was HTML and      * invoke the provided observer as links are encountered.      * @param url   the URL to parse for links      * @param observer  the callback object      * @param data  client-specific data; this is passed back to the      *              client in callbacks; this scanner doesn't use it      * @throws Exception    see hplb.org.xml.sax.Parser.parse()      * @see hplb.org.xml.sax.Parser.parse      */
DECL|method|HtmlScanner
specifier|public
name|HtmlScanner
parameter_list|(
name|URL
name|url
parameter_list|,
name|HtmlObserver
name|observer
parameter_list|)
throws|throws
name|Exception
block|{
name|this
argument_list|(
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|url
operator|.
name|openStream
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|url
argument_list|,
name|observer
argument_list|)
expr_stmt|;
block|}
comment|/**      * Parse the input on the specified stream as if it was HTML and      * invoke the provided observer as links are encountered.      * @param in    the input stream      * @param url   the URL corresponding to this document      * @param observer  the callback object      * @throws Exception    see hplb.org.xml.sax.Parser.parse()      * @see hplb.org.xml.sax.Parser.parse 	 * @deprecated      */
DECL|method|HtmlScanner
specifier|public
name|HtmlScanner
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|URL
name|url
parameter_list|,
name|HtmlObserver
name|observer
parameter_list|)
throws|throws
name|Exception
block|{
name|this
argument_list|(
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|)
argument_list|)
argument_list|,
name|url
argument_list|,
name|observer
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Parse the input on the specified stream as if it was HTML and      * invoke the provided observer as links are encountered.      * @param in    the Reader      * @param url   the URL corresponding to this document      * @param observer  the callback object      * @throws Exception    see hplb.org.xml.sax.Parser.parse()      * @see hplb.org.xml.sax.Parser.parse      */
DECL|method|HtmlScanner
specifier|public
name|HtmlScanner
parameter_list|(
name|Reader
name|in
parameter_list|,
name|URL
name|url
parameter_list|,
name|HtmlObserver
name|observer
parameter_list|)
throws|throws
name|Exception
block|{
name|this
argument_list|(
name|in
argument_list|,
name|url
argument_list|,
name|observer
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Parse the input on the specified stream as if it was HTML and      * invoke the provided observer as links are encountered. 	 * Although not deprecated, this method should not be used. Use HtmlScanner(Reader...) instead 	 * @deprecated 	 */
DECL|method|HtmlScanner
specifier|public
name|HtmlScanner
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|URL
name|url
parameter_list|,
name|HtmlObserver
name|observer
parameter_list|,
name|Object
name|data
parameter_list|)
throws|throws
name|Exception
block|{
name|this
argument_list|(
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|)
argument_list|)
argument_list|,
name|url
argument_list|,
name|observer
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
comment|/**      * Parse the input on the specified stream as if it was HTML and      * invoke the provided observer as links are encountered.      * @param in    the input stream      * @param url   the URL corresponding to this document      * @param observer  the callback object      * @param data  client-specific data; this is passed back to the      *              client in callbacks; this scanner doesn't use it      * @throws Exception    see hplb.org.xml.sax.Parser.parse()      * @see hplb.org.xml.sax.Parser.parse      */
DECL|method|HtmlScanner
specifier|public
name|HtmlScanner
parameter_list|(
name|Reader
name|in
parameter_list|,
name|URL
name|url
parameter_list|,
name|HtmlObserver
name|observer
parameter_list|,
name|Object
name|data
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|observer
operator|=
name|observer
expr_stmt|;
name|this
operator|.
name|contextURL
operator|=
name|url
expr_stmt|;
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
name|tok
operator|=
operator|new
name|Tokenizer
argument_list|()
expr_stmt|;
name|setDocumentHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|HTML
operator|.
name|applyHacks
argument_list|(
name|tok
argument_list|)
expr_stmt|;
name|tok
operator|.
name|rcgnzEntities
operator|=
literal|false
expr_stmt|;
name|tok
operator|.
name|rcgnzCDATA
operator|=
literal|false
expr_stmt|;
name|tok
operator|.
name|atomize
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|setDocumentHandler
specifier|public
name|void
name|setDocumentHandler
parameter_list|(
name|DocumentHandler
name|doc
parameter_list|)
block|{
name|tok
operator|.
name|setDocumentHandler
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|setEntityHandler
specifier|public
name|void
name|setEntityHandler
parameter_list|(
name|EntityHandler
name|ent
parameter_list|)
block|{
name|tok
operator|.
name|setEntityHandler
argument_list|(
name|ent
argument_list|)
expr_stmt|;
block|}
DECL|method|setErrorHandler
specifier|public
name|void
name|setErrorHandler
parameter_list|(
name|ErrorHandler
name|err
parameter_list|)
block|{
name|tok
operator|.
name|setErrorHandler
argument_list|(
name|err
argument_list|)
expr_stmt|;
block|}
DECL|method|parse
specifier|public
name|void
name|parse
parameter_list|()
throws|throws
name|Exception
block|{
name|tok
operator|.
name|parse
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
DECL|method|startElement
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|name
parameter_list|,
name|AttributeMap
name|attributes
parameter_list|)
block|{
name|String
name|val
decl_stmt|;
if|if
condition|(
name|name
operator|==
name|HTML
operator|.
name|A
condition|)
block|{
if|if
condition|(
operator|(
name|val
operator|=
name|attributes
operator|.
name|getValue
argument_list|(
literal|"href"
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|observer
operator|.
name|gotAHref
argument_list|(
name|val
argument_list|,
name|contextURL
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|name
operator|==
name|HTML
operator|.
name|IMG
condition|)
block|{
if|if
condition|(
operator|(
name|val
operator|=
name|attributes
operator|.
name|getValue
argument_list|(
literal|"src"
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|observer
operator|.
name|gotImgSrc
argument_list|(
name|val
argument_list|,
name|contextURL
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|name
operator|==
name|HTML
operator|.
name|BASE
condition|)
block|{
if|if
condition|(
operator|(
name|val
operator|=
name|attributes
operator|.
name|getValue
argument_list|(
literal|"href"
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|observer
operator|.
name|gotBaseHref
argument_list|(
name|val
argument_list|,
name|contextURL
argument_list|,
name|data
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextURL
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|contextURL
operator|=
operator|new
name|URL
argument_list|(
name|contextURL
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|ex
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Bad<base> URL: "
operator|+
name|val
operator|+
literal|"."
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|name
operator|==
name|HTML
operator|.
name|AREA
condition|)
block|{
if|if
condition|(
operator|(
name|val
operator|=
name|attributes
operator|.
name|getValue
argument_list|(
literal|"href"
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|observer
operator|.
name|gotAreaHref
argument_list|(
name|val
argument_list|,
name|contextURL
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|name
operator|==
name|HTML
operator|.
name|FRAME
condition|)
block|{
if|if
condition|(
operator|(
name|val
operator|=
name|attributes
operator|.
name|getValue
argument_list|(
literal|"src"
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|observer
operator|.
name|gotFrameSrc
argument_list|(
name|val
argument_list|,
name|contextURL
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

