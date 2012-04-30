begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|ResourceLoader
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
name|EntityResolver
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
name|EntityResolver2
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Source
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|URIResolver
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|sax
operator|.
name|SAXSource
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLResolver
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
import|;
end_import

begin_comment
comment|/**  * This is a helper class to support resolving of XIncludes or other hrefs  * inside XML files on top of a {@link ResourceLoader}. Just plug this class  * on top of a {@link ResourceLoader} and pass it as {@link EntityResolver} to SAX parsers  * or via wrapper methods as {@link URIResolver} to XSL transformers or {@link XMLResolver} to STAX parsers.  * The resolver handles special SystemIds with an URI scheme of {@code solrres:} that point  * to resources. To produce such systemIds when you initially call the parser, use  * {@link #createSystemIdFromResourceName} which produces a SystemId that can  * be included along the InputStream coming from {@link ResourceLoader#openResource}.  *<p>In general create the {@link InputSource} to be passed to the parser like:</p>  *<pre class="prettyprint">  *  InputSource is = new InputSource(loader.openSchema(name));  *  is.setSystemId(SystemIdResolver.createSystemIdFromResourceName(name));  *  final DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();  *  db.setEntityResolver(new SystemIdResolver(loader));  *  Document doc = db.parse(is);  *</pre>  */
end_comment

begin_class
DECL|class|SystemIdResolver
specifier|public
specifier|final
class|class
name|SystemIdResolver
implements|implements
name|EntityResolver
implements|,
name|EntityResolver2
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SystemIdResolver
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|RESOURCE_LOADER_URI_SCHEME
specifier|public
specifier|static
specifier|final
name|String
name|RESOURCE_LOADER_URI_SCHEME
init|=
literal|"solrres"
decl_stmt|;
DECL|field|RESOURCE_LOADER_AUTHORITY_ABSOLUTE
specifier|public
specifier|static
specifier|final
name|String
name|RESOURCE_LOADER_AUTHORITY_ABSOLUTE
init|=
literal|"@"
decl_stmt|;
DECL|field|loader
specifier|private
specifier|final
name|ResourceLoader
name|loader
decl_stmt|;
DECL|method|SystemIdResolver
specifier|public
name|SystemIdResolver
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|)
block|{
name|this
operator|.
name|loader
operator|=
name|loader
expr_stmt|;
block|}
DECL|method|asEntityResolver
specifier|public
name|EntityResolver
name|asEntityResolver
parameter_list|()
block|{
return|return
name|this
return|;
block|}
DECL|method|asURIResolver
specifier|public
name|URIResolver
name|asURIResolver
parameter_list|()
block|{
return|return
operator|new
name|URIResolver
argument_list|()
block|{
specifier|public
name|Source
name|resolve
parameter_list|(
name|String
name|href
parameter_list|,
name|String
name|base
parameter_list|)
throws|throws
name|TransformerException
block|{
try|try
block|{
specifier|final
name|InputSource
name|src
init|=
name|SystemIdResolver
operator|.
name|this
operator|.
name|resolveEntity
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|base
argument_list|,
name|href
argument_list|)
decl_stmt|;
return|return
operator|(
name|src
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
operator|new
name|SAXSource
argument_list|(
name|src
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
literal|"Cannot resolve entity"
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
DECL|method|asXMLResolver
specifier|public
name|XMLResolver
name|asXMLResolver
parameter_list|()
block|{
return|return
operator|new
name|XMLResolver
argument_list|()
block|{
specifier|public
name|Object
name|resolveEntity
parameter_list|(
name|String
name|publicId
parameter_list|,
name|String
name|systemId
parameter_list|,
name|String
name|baseURI
parameter_list|,
name|String
name|namespace
parameter_list|)
throws|throws
name|XMLStreamException
block|{
try|try
block|{
specifier|final
name|InputSource
name|src
init|=
name|SystemIdResolver
operator|.
name|this
operator|.
name|resolveEntity
argument_list|(
literal|null
argument_list|,
name|publicId
argument_list|,
name|baseURI
argument_list|,
name|systemId
argument_list|)
decl_stmt|;
return|return
operator|(
name|src
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|src
operator|.
name|getByteStream
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|XMLStreamException
argument_list|(
literal|"Cannot resolve entity"
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
DECL|method|resolveRelativeURI
name|URI
name|resolveRelativeURI
parameter_list|(
name|String
name|baseURI
parameter_list|,
name|String
name|systemId
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|URI
name|uri
decl_stmt|;
comment|// special case for backwards compatibility: if relative systemId starts with "/" (we convert that to an absolute solrres:-URI)
if|if
condition|(
name|systemId
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|uri
operator|=
operator|new
name|URI
argument_list|(
name|RESOURCE_LOADER_URI_SCHEME
argument_list|,
name|RESOURCE_LOADER_AUTHORITY_ABSOLUTE
argument_list|,
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|resolve
argument_list|(
name|systemId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// simply parse as URI
name|uri
operator|=
operator|new
name|URI
argument_list|(
name|systemId
argument_list|)
expr_stmt|;
block|}
comment|// do relative resolving
if|if
condition|(
name|baseURI
operator|!=
literal|null
condition|)
block|{
name|uri
operator|=
operator|new
name|URI
argument_list|(
name|baseURI
argument_list|)
operator|.
name|resolve
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
return|return
name|uri
return|;
block|}
comment|// *** EntityResolver(2) methods:
DECL|method|getExternalSubset
specifier|public
name|InputSource
name|getExternalSubset
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|baseURI
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
DECL|method|resolveEntity
specifier|public
name|InputSource
name|resolveEntity
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|publicId
parameter_list|,
name|String
name|baseURI
parameter_list|,
name|String
name|systemId
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|systemId
operator|==
literal|null
condition|)
return|return
literal|null
return|;
try|try
block|{
specifier|final
name|URI
name|uri
init|=
name|resolveRelativeURI
argument_list|(
name|baseURI
argument_list|,
name|systemId
argument_list|)
decl_stmt|;
comment|// check schema and resolve with ResourceLoader
if|if
condition|(
name|RESOURCE_LOADER_URI_SCHEME
operator|.
name|equals
argument_list|(
name|uri
operator|.
name|getScheme
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|path
init|=
name|uri
operator|.
name|getPath
argument_list|()
decl_stmt|,
name|authority
init|=
name|uri
operator|.
name|getAuthority
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|RESOURCE_LOADER_AUTHORITY_ABSOLUTE
operator|.
name|equals
argument_list|(
name|authority
argument_list|)
condition|)
block|{
name|path
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
try|try
block|{
specifier|final
name|InputSource
name|is
init|=
operator|new
name|InputSource
argument_list|(
name|loader
operator|.
name|openResource
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|is
operator|.
name|setSystemId
argument_list|(
name|uri
operator|.
name|toASCIIString
argument_list|()
argument_list|)
expr_stmt|;
name|is
operator|.
name|setPublicId
argument_list|(
name|publicId
argument_list|)
expr_stmt|;
return|return
name|is
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|re
parameter_list|)
block|{
comment|// unfortunately XInclude fallback only works with IOException, but openResource() never throws that one
throw|throw
call|(
name|IOException
call|)
argument_list|(
operator|new
name|IOException
argument_list|(
name|re
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|initCause
argument_list|(
name|re
argument_list|)
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// resolve all other URIs using the standard resolver
return|return
literal|null
return|;
block|}
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|use
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"An URI systax problem occurred during resolving SystemId, falling back to default resolver"
argument_list|,
name|use
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|resolveEntity
specifier|public
name|InputSource
name|resolveEntity
parameter_list|(
name|String
name|publicId
parameter_list|,
name|String
name|systemId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|resolveEntity
argument_list|(
literal|null
argument_list|,
name|publicId
argument_list|,
literal|null
argument_list|,
name|systemId
argument_list|)
return|;
block|}
DECL|method|createSystemIdFromResourceName
specifier|public
specifier|static
name|String
name|createSystemIdFromResourceName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|name
operator|=
name|name
operator|.
name|replace
argument_list|(
name|File
operator|.
name|separatorChar
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
specifier|final
name|String
name|authority
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
comment|// a hack to preserve absolute filenames and keep them absolute after resolving, we set the URI's authority to "@" on absolute filenames:
name|authority
operator|=
name|RESOURCE_LOADER_AUTHORITY_ABSOLUTE
expr_stmt|;
block|}
else|else
block|{
name|authority
operator|=
literal|null
expr_stmt|;
name|name
operator|=
literal|"/"
operator|+
name|name
expr_stmt|;
block|}
try|try
block|{
return|return
operator|new
name|URI
argument_list|(
name|RESOURCE_LOADER_URI_SCHEME
argument_list|,
name|authority
argument_list|,
name|name
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|toASCIIString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|use
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid syntax of Solr Resource URI"
argument_list|,
name|use
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

