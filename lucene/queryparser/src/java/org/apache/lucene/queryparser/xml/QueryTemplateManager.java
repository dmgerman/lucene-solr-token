begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.xml
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|xml
package|;
end_package

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
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
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
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
name|*
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
name|dom
operator|.
name|DOMResult
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
name|dom
operator|.
name|DOMSource
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
name|StringWriter
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|Properties
import|;
end_import

begin_comment
comment|/**  * Provides utilities for turning query form input (such as from a web page or Swing gui) into  * Lucene XML queries by using XSL templates.  This approach offers a convenient way of externalizing  * and changing how user input is turned into Lucene queries.  * Database applications often adopt similar practices by externalizing SQL in template files that can  * be easily changed/optimized by a DBA.  * The static methods can be used on their own or by creating an instance of this class you can store and  * re-use compiled stylesheets for fast use (e.g. in a server environment)  */
end_comment

begin_class
DECL|class|QueryTemplateManager
specifier|public
class|class
name|QueryTemplateManager
block|{
DECL|field|dbf
specifier|static
specifier|final
name|DocumentBuilderFactory
name|dbf
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
DECL|field|tFactory
specifier|static
specifier|final
name|TransformerFactory
name|tFactory
init|=
name|TransformerFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
DECL|field|compiledTemplatesCache
name|HashMap
argument_list|<
name|String
argument_list|,
name|Templates
argument_list|>
name|compiledTemplatesCache
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|defaultCompiledTemplates
name|Templates
name|defaultCompiledTemplates
init|=
literal|null
decl_stmt|;
DECL|method|QueryTemplateManager
specifier|public
name|QueryTemplateManager
parameter_list|()
block|{    }
DECL|method|QueryTemplateManager
specifier|public
name|QueryTemplateManager
parameter_list|(
name|InputStream
name|xslIs
parameter_list|)
throws|throws
name|TransformerConfigurationException
throws|,
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|IOException
block|{
name|addDefaultQueryTemplate
argument_list|(
name|xslIs
argument_list|)
expr_stmt|;
block|}
DECL|method|addDefaultQueryTemplate
specifier|public
name|void
name|addDefaultQueryTemplate
parameter_list|(
name|InputStream
name|xslIs
parameter_list|)
throws|throws
name|TransformerConfigurationException
throws|,
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|IOException
block|{
name|defaultCompiledTemplates
operator|=
name|getTemplates
argument_list|(
name|xslIs
argument_list|)
expr_stmt|;
block|}
DECL|method|addQueryTemplate
specifier|public
name|void
name|addQueryTemplate
parameter_list|(
name|String
name|name
parameter_list|,
name|InputStream
name|xslIs
parameter_list|)
throws|throws
name|TransformerConfigurationException
throws|,
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|IOException
block|{
name|compiledTemplatesCache
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|getTemplates
argument_list|(
name|xslIs
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getQueryAsXmlString
specifier|public
name|String
name|getQueryAsXmlString
parameter_list|(
name|Properties
name|formProperties
parameter_list|,
name|String
name|queryTemplateName
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
throws|,
name|ParserConfigurationException
throws|,
name|TransformerException
block|{
name|Templates
name|ts
init|=
name|compiledTemplatesCache
operator|.
name|get
argument_list|(
name|queryTemplateName
argument_list|)
decl_stmt|;
return|return
name|getQueryAsXmlString
argument_list|(
name|formProperties
argument_list|,
name|ts
argument_list|)
return|;
block|}
DECL|method|getQueryAsDOM
specifier|public
name|Document
name|getQueryAsDOM
parameter_list|(
name|Properties
name|formProperties
parameter_list|,
name|String
name|queryTemplateName
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
throws|,
name|ParserConfigurationException
throws|,
name|TransformerException
block|{
name|Templates
name|ts
init|=
name|compiledTemplatesCache
operator|.
name|get
argument_list|(
name|queryTemplateName
argument_list|)
decl_stmt|;
return|return
name|getQueryAsDOM
argument_list|(
name|formProperties
argument_list|,
name|ts
argument_list|)
return|;
block|}
DECL|method|getQueryAsXmlString
specifier|public
name|String
name|getQueryAsXmlString
parameter_list|(
name|Properties
name|formProperties
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
throws|,
name|ParserConfigurationException
throws|,
name|TransformerException
block|{
return|return
name|getQueryAsXmlString
argument_list|(
name|formProperties
argument_list|,
name|defaultCompiledTemplates
argument_list|)
return|;
block|}
DECL|method|getQueryAsDOM
specifier|public
name|Document
name|getQueryAsDOM
parameter_list|(
name|Properties
name|formProperties
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
throws|,
name|ParserConfigurationException
throws|,
name|TransformerException
block|{
return|return
name|getQueryAsDOM
argument_list|(
name|formProperties
argument_list|,
name|defaultCompiledTemplates
argument_list|)
return|;
block|}
comment|/**    * Fast means of constructing query using a precompiled stylesheet    */
DECL|method|getQueryAsXmlString
specifier|public
specifier|static
name|String
name|getQueryAsXmlString
parameter_list|(
name|Properties
name|formProperties
parameter_list|,
name|Templates
name|template
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|TransformerException
block|{
comment|// TODO: Suppress XML header with encoding (as Strings have no encoding)
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|StreamResult
name|result
init|=
operator|new
name|StreamResult
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|transformCriteria
argument_list|(
name|formProperties
argument_list|,
name|template
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Slow means of constructing query parsing a stylesheet from an input stream    */
DECL|method|getQueryAsXmlString
specifier|public
specifier|static
name|String
name|getQueryAsXmlString
parameter_list|(
name|Properties
name|formProperties
parameter_list|,
name|InputStream
name|xslIs
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
throws|,
name|ParserConfigurationException
throws|,
name|TransformerException
block|{
comment|// TODO: Suppress XML header with encoding (as Strings have no encoding)
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|StreamResult
name|result
init|=
operator|new
name|StreamResult
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|transformCriteria
argument_list|(
name|formProperties
argument_list|,
name|xslIs
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Fast means of constructing query using a cached,precompiled stylesheet    */
DECL|method|getQueryAsDOM
specifier|public
specifier|static
name|Document
name|getQueryAsDOM
parameter_list|(
name|Properties
name|formProperties
parameter_list|,
name|Templates
name|template
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|TransformerException
block|{
name|DOMResult
name|result
init|=
operator|new
name|DOMResult
argument_list|()
decl_stmt|;
name|transformCriteria
argument_list|(
name|formProperties
argument_list|,
name|template
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
operator|(
name|Document
operator|)
name|result
operator|.
name|getNode
argument_list|()
return|;
block|}
comment|/**    * Slow means of constructing query - parses stylesheet from input stream    */
DECL|method|getQueryAsDOM
specifier|public
specifier|static
name|Document
name|getQueryAsDOM
parameter_list|(
name|Properties
name|formProperties
parameter_list|,
name|InputStream
name|xslIs
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
throws|,
name|ParserConfigurationException
throws|,
name|TransformerException
block|{
name|DOMResult
name|result
init|=
operator|new
name|DOMResult
argument_list|()
decl_stmt|;
name|transformCriteria
argument_list|(
name|formProperties
argument_list|,
name|xslIs
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
operator|(
name|Document
operator|)
name|result
operator|.
name|getNode
argument_list|()
return|;
block|}
comment|/**    * Slower transformation using an uncompiled stylesheet (suitable for development environment)    */
DECL|method|transformCriteria
specifier|public
specifier|static
name|void
name|transformCriteria
parameter_list|(
name|Properties
name|formProperties
parameter_list|,
name|InputStream
name|xslIs
parameter_list|,
name|Result
name|result
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
throws|,
name|ParserConfigurationException
throws|,
name|TransformerException
block|{
name|dbf
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|DocumentBuilder
name|builder
init|=
name|dbf
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
name|xslDoc
init|=
name|builder
operator|.
name|parse
argument_list|(
name|xslIs
argument_list|)
decl_stmt|;
name|DOMSource
name|ds
init|=
operator|new
name|DOMSource
argument_list|(
name|xslDoc
argument_list|)
decl_stmt|;
name|Transformer
name|transformer
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|tFactory
init|)
block|{
name|transformer
operator|=
name|tFactory
operator|.
name|newTransformer
argument_list|(
name|ds
argument_list|)
expr_stmt|;
block|}
name|transformCriteria
argument_list|(
name|formProperties
argument_list|,
name|transformer
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
comment|/**    * Fast transformation using a pre-compiled stylesheet (suitable for production environments)    */
DECL|method|transformCriteria
specifier|public
specifier|static
name|void
name|transformCriteria
parameter_list|(
name|Properties
name|formProperties
parameter_list|,
name|Templates
name|template
parameter_list|,
name|Result
name|result
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|TransformerException
block|{
name|transformCriteria
argument_list|(
name|formProperties
argument_list|,
name|template
operator|.
name|newTransformer
argument_list|()
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
DECL|method|transformCriteria
specifier|public
specifier|static
name|void
name|transformCriteria
parameter_list|(
name|Properties
name|formProperties
parameter_list|,
name|Transformer
name|transformer
parameter_list|,
name|Result
name|result
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|TransformerException
block|{
name|dbf
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//Create an XML document representing the search index document.
name|DocumentBuilder
name|db
init|=
name|dbf
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
name|doc
init|=
name|db
operator|.
name|newDocument
argument_list|()
decl_stmt|;
name|Element
name|root
init|=
name|doc
operator|.
name|createElement
argument_list|(
literal|"Document"
argument_list|)
decl_stmt|;
name|doc
operator|.
name|appendChild
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|Enumeration
argument_list|<
name|?
argument_list|>
name|keysEnum
init|=
name|formProperties
operator|.
name|propertyNames
argument_list|()
decl_stmt|;
while|while
condition|(
name|keysEnum
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|propName
init|=
name|keysEnum
operator|.
name|nextElement
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|formProperties
operator|.
name|getProperty
argument_list|(
name|propName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|value
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|value
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|)
condition|)
block|{
name|DOMUtils
operator|.
name|insertChild
argument_list|(
name|root
argument_list|,
name|propName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
comment|//Use XSLT to to transform into an XML query string using the  queryTemplate
name|DOMSource
name|xml
init|=
operator|new
name|DOMSource
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|transformer
operator|.
name|transform
argument_list|(
name|xml
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
comment|/**    * Parses a query stylesheet for repeated use    */
DECL|method|getTemplates
specifier|public
specifier|static
name|Templates
name|getTemplates
parameter_list|(
name|InputStream
name|xslIs
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|IOException
throws|,
name|TransformerConfigurationException
block|{
name|dbf
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|DocumentBuilder
name|builder
init|=
name|dbf
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
name|xslDoc
init|=
name|builder
operator|.
name|parse
argument_list|(
name|xslIs
argument_list|)
decl_stmt|;
name|DOMSource
name|ds
init|=
operator|new
name|DOMSource
argument_list|(
name|xslDoc
argument_list|)
decl_stmt|;
return|return
name|tFactory
operator|.
name|newTemplates
argument_list|(
name|ds
argument_list|)
return|;
block|}
block|}
end_class

end_unit

