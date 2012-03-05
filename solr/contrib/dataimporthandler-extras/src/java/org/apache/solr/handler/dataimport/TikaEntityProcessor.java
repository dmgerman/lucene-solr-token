begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|config
operator|.
name|TikaConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|Metadata
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|AutoDetectParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|ParseContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|Parser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|sax
operator|.
name|BodyContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|sax
operator|.
name|ContentHandlerDecorator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|sax
operator|.
name|XHTMLContentHandler
import|;
end_import

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
name|ContentHandler
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
name|helpers
operator|.
name|DefaultHandler
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
name|OutputKeys
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
name|TransformerConfigurationException
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
name|TransformerFactory
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
name|SAXTransformerFactory
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
name|TransformerHandler
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
name|stream
operator|.
name|StreamResult
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|DataImportHandlerException
operator|.
name|SEVERE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|DataImportHandlerException
operator|.
name|wrapAndThrow
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|DataImporter
operator|.
name|COLUMN
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|XPathEntityProcessor
operator|.
name|URL
import|;
end_import

begin_comment
comment|/**  *<p>An implementation of {@link EntityProcessor} which reads data from rich docs  * using<a href="http://tika.apache.org/">Apache Tika</a>  *  *  * @since solr 3.1  */
end_comment

begin_class
DECL|class|TikaEntityProcessor
specifier|public
class|class
name|TikaEntityProcessor
extends|extends
name|EntityProcessorBase
block|{
DECL|field|tikaConfig
specifier|private
name|TikaConfig
name|tikaConfig
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TikaEntityProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|format
specifier|private
name|String
name|format
init|=
literal|"text"
decl_stmt|;
DECL|field|done
specifier|private
name|boolean
name|done
init|=
literal|false
decl_stmt|;
DECL|field|parser
specifier|private
name|String
name|parser
decl_stmt|;
DECL|field|AUTO_PARSER
specifier|static
specifier|final
name|String
name|AUTO_PARSER
init|=
literal|"org.apache.tika.parser.AutoDetectParser"
decl_stmt|;
annotation|@
name|Override
DECL|method|firstInit
specifier|protected
name|void
name|firstInit
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
try|try
block|{
name|String
name|tikaConfigFile
init|=
name|context
operator|.
name|getResolvedEntityAttribute
argument_list|(
literal|"tikaConfig"
argument_list|)
decl_stmt|;
if|if
condition|(
name|tikaConfigFile
operator|==
literal|null
condition|)
block|{
name|ClassLoader
name|classLoader
init|=
name|context
operator|.
name|getSolrCore
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
name|tikaConfig
operator|=
operator|new
name|TikaConfig
argument_list|(
name|classLoader
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|File
name|configFile
init|=
operator|new
name|File
argument_list|(
name|tikaConfigFile
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|configFile
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|configFile
operator|=
operator|new
name|File
argument_list|(
name|context
operator|.
name|getSolrCore
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getConfigDir
argument_list|()
argument_list|,
name|tikaConfigFile
argument_list|)
expr_stmt|;
block|}
name|tikaConfig
operator|=
operator|new
name|TikaConfig
argument_list|(
name|configFile
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|,
literal|"Unable to load Tika Config"
argument_list|)
expr_stmt|;
block|}
name|format
operator|=
name|context
operator|.
name|getResolvedEntityAttribute
argument_list|(
literal|"format"
argument_list|)
expr_stmt|;
if|if
condition|(
name|format
operator|==
literal|null
condition|)
name|format
operator|=
literal|"text"
expr_stmt|;
if|if
condition|(
operator|!
literal|"html"
operator|.
name|equals
argument_list|(
name|format
argument_list|)
operator|&&
operator|!
literal|"xml"
operator|.
name|equals
argument_list|(
name|format
argument_list|)
operator|&&
operator|!
literal|"text"
operator|.
name|equals
argument_list|(
name|format
argument_list|)
operator|&&
operator|!
literal|"none"
operator|.
name|equals
argument_list|(
name|format
argument_list|)
condition|)
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"'format' can be one of text|html|xml|none"
argument_list|)
throw|;
name|parser
operator|=
name|context
operator|.
name|getResolvedEntityAttribute
argument_list|(
literal|"parser"
argument_list|)
expr_stmt|;
if|if
condition|(
name|parser
operator|==
literal|null
condition|)
block|{
name|parser
operator|=
name|AUTO_PARSER
expr_stmt|;
block|}
name|done
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|nextRow
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextRow
parameter_list|()
block|{
if|if
condition|(
name|done
condition|)
return|return
literal|null
return|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|DataSource
argument_list|<
name|InputStream
argument_list|>
name|dataSource
init|=
name|context
operator|.
name|getDataSource
argument_list|()
decl_stmt|;
name|InputStream
name|is
init|=
name|dataSource
operator|.
name|getData
argument_list|(
name|context
operator|.
name|getResolvedEntityAttribute
argument_list|(
name|URL
argument_list|)
argument_list|)
decl_stmt|;
name|ContentHandler
name|contentHandler
init|=
literal|null
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
literal|"html"
operator|.
name|equals
argument_list|(
name|format
argument_list|)
condition|)
block|{
name|contentHandler
operator|=
name|getHtmlHandler
argument_list|(
name|sw
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"xml"
operator|.
name|equals
argument_list|(
name|format
argument_list|)
condition|)
block|{
name|contentHandler
operator|=
name|getXmlContentHandler
argument_list|(
name|sw
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"text"
operator|.
name|equals
argument_list|(
name|format
argument_list|)
condition|)
block|{
name|contentHandler
operator|=
name|getTextContentHandler
argument_list|(
name|sw
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"none"
operator|.
name|equals
argument_list|(
name|format
argument_list|)
condition|)
block|{
name|contentHandler
operator|=
operator|new
name|DefaultHandler
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|TransformerConfigurationException
name|e
parameter_list|)
block|{
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|,
literal|"Unable to create content handler"
argument_list|)
expr_stmt|;
block|}
name|Parser
name|tikaParser
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|parser
operator|.
name|equals
argument_list|(
name|AUTO_PARSER
argument_list|)
condition|)
block|{
name|tikaParser
operator|=
operator|new
name|AutoDetectParser
argument_list|(
name|tikaConfig
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tikaParser
operator|=
operator|(
name|Parser
operator|)
name|context
operator|.
name|getSolrCore
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|newInstance
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|tikaParser
operator|.
name|parse
argument_list|(
name|is
argument_list|,
name|contentHandler
argument_list|,
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|,
literal|"Unable to read content"
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|field
range|:
name|context
operator|.
name|getAllEntityFields
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
literal|"true"
operator|.
name|equals
argument_list|(
name|field
operator|.
name|get
argument_list|(
literal|"meta"
argument_list|)
argument_list|)
condition|)
continue|continue;
name|String
name|col
init|=
name|field
operator|.
name|get
argument_list|(
name|COLUMN
argument_list|)
decl_stmt|;
name|String
name|s
init|=
name|metadata
operator|.
name|get
argument_list|(
name|col
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
name|row
operator|.
name|put
argument_list|(
name|col
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
literal|"none"
operator|.
name|equals
argument_list|(
name|format
argument_list|)
condition|)
name|row
operator|.
name|put
argument_list|(
literal|"text"
argument_list|,
name|sw
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|done
operator|=
literal|true
expr_stmt|;
return|return
name|row
return|;
block|}
DECL|method|getHtmlHandler
specifier|private
specifier|static
name|ContentHandler
name|getHtmlHandler
parameter_list|(
name|Writer
name|writer
parameter_list|)
throws|throws
name|TransformerConfigurationException
block|{
name|SAXTransformerFactory
name|factory
init|=
operator|(
name|SAXTransformerFactory
operator|)
name|TransformerFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|TransformerHandler
name|handler
init|=
name|factory
operator|.
name|newTransformerHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|getTransformer
argument_list|()
operator|.
name|setOutputProperty
argument_list|(
name|OutputKeys
operator|.
name|METHOD
argument_list|,
literal|"html"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|setResult
argument_list|(
operator|new
name|StreamResult
argument_list|(
name|writer
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|ContentHandlerDecorator
argument_list|(
name|handler
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|name
parameter_list|,
name|Attributes
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|XHTMLContentHandler
operator|.
name|XHTML
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
name|uri
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
operator|!
literal|"head"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|super
operator|.
name|startElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|name
argument_list|,
name|atts
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|XHTMLContentHandler
operator|.
name|XHTML
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
name|uri
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
operator|!
literal|"head"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|super
operator|.
name|endElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|startPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
block|{
comment|/*no op*/
block|}
annotation|@
name|Override
specifier|public
name|void
name|endPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
comment|/*no op*/
block|}
block|}
return|;
block|}
DECL|method|getTextContentHandler
specifier|private
specifier|static
name|ContentHandler
name|getTextContentHandler
parameter_list|(
name|Writer
name|writer
parameter_list|)
block|{
return|return
operator|new
name|BodyContentHandler
argument_list|(
name|writer
argument_list|)
return|;
block|}
DECL|method|getXmlContentHandler
specifier|private
specifier|static
name|ContentHandler
name|getXmlContentHandler
parameter_list|(
name|Writer
name|writer
parameter_list|)
throws|throws
name|TransformerConfigurationException
block|{
name|SAXTransformerFactory
name|factory
init|=
operator|(
name|SAXTransformerFactory
operator|)
name|TransformerFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|TransformerHandler
name|handler
init|=
name|factory
operator|.
name|newTransformerHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|getTransformer
argument_list|()
operator|.
name|setOutputProperty
argument_list|(
name|OutputKeys
operator|.
name|METHOD
argument_list|,
literal|"xml"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|setResult
argument_list|(
operator|new
name|StreamResult
argument_list|(
name|writer
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|handler
return|;
block|}
block|}
end_class

end_unit

