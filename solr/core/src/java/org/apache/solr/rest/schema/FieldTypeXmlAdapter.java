begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.rest.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
operator|.
name|schema
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
operator|.
name|ErrorCode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|CommonParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|IndexSchema
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|SimilarityFactory
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
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_comment
comment|/**  * Utility class for converting a JSON definition of a FieldType into the  * XML format expected by the FieldTypePluginLoader.  */
end_comment

begin_class
DECL|class|FieldTypeXmlAdapter
specifier|public
class|class
name|FieldTypeXmlAdapter
block|{
DECL|method|toNode
specifier|public
specifier|static
name|Node
name|toNode
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|json
parameter_list|)
block|{
name|DocumentBuilder
name|docBuilder
decl_stmt|;
try|try
block|{
name|docBuilder
operator|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newDocumentBuilder
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|Document
name|doc
init|=
name|docBuilder
operator|.
name|newDocument
argument_list|()
decl_stmt|;
name|Element
name|fieldType
init|=
name|doc
operator|.
name|createElement
argument_list|(
name|IndexSchema
operator|.
name|FIELD_TYPE
argument_list|)
decl_stmt|;
name|appendAttrs
argument_list|(
name|fieldType
argument_list|,
name|json
argument_list|)
expr_stmt|;
comment|// transform the analyzer definitions into XML elements
name|Element
name|analyzer
init|=
name|transformAnalyzer
argument_list|(
name|doc
argument_list|,
name|json
argument_list|,
literal|"analyzer"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|analyzer
operator|!=
literal|null
condition|)
name|fieldType
operator|.
name|appendChild
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|analyzer
operator|=
name|transformAnalyzer
argument_list|(
name|doc
argument_list|,
name|json
argument_list|,
literal|"indexAnalyzer"
argument_list|,
literal|"index"
argument_list|)
expr_stmt|;
if|if
condition|(
name|analyzer
operator|!=
literal|null
condition|)
name|fieldType
operator|.
name|appendChild
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|analyzer
operator|=
name|transformAnalyzer
argument_list|(
name|doc
argument_list|,
name|json
argument_list|,
literal|"queryAnalyzer"
argument_list|,
literal|"query"
argument_list|)
expr_stmt|;
if|if
condition|(
name|analyzer
operator|!=
literal|null
condition|)
name|fieldType
operator|.
name|appendChild
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|analyzer
operator|=
name|transformAnalyzer
argument_list|(
name|doc
argument_list|,
name|json
argument_list|,
literal|"multiTermAnalyzer"
argument_list|,
literal|"multiterm"
argument_list|)
expr_stmt|;
if|if
condition|(
name|analyzer
operator|!=
literal|null
condition|)
name|fieldType
operator|.
name|appendChild
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|Element
name|similarity
init|=
name|transformSimilarity
argument_list|(
name|doc
argument_list|,
name|json
argument_list|,
literal|"similarity"
argument_list|)
decl_stmt|;
if|if
condition|(
name|similarity
operator|!=
literal|null
condition|)
name|fieldType
operator|.
name|appendChild
argument_list|(
name|similarity
argument_list|)
expr_stmt|;
return|return
name|fieldType
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|transformSimilarity
specifier|protected
specifier|static
name|Element
name|transformSimilarity
parameter_list|(
name|Document
name|doc
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|json
parameter_list|,
name|String
name|jsonFieldName
parameter_list|)
block|{
name|Object
name|jsonField
init|=
name|json
operator|.
name|get
argument_list|(
name|jsonFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|jsonField
operator|==
literal|null
condition|)
return|return
literal|null
return|;
comment|// it's ok for this field to not exist in the JSON map
if|if
condition|(
operator|!
operator|(
name|jsonField
operator|instanceof
name|Map
operator|)
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Invalid fieldType definition! Expected JSON object for "
operator|+
name|jsonFieldName
operator|+
literal|" not a "
operator|+
name|jsonField
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
name|Element
name|similarity
init|=
name|doc
operator|.
name|createElement
argument_list|(
literal|"similarity"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|config
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
operator|)
name|jsonField
decl_stmt|;
name|similarity
operator|.
name|setAttribute
argument_list|(
name|SimilarityFactory
operator|.
name|CLASS_NAME
argument_list|,
operator|(
name|String
operator|)
name|config
operator|.
name|remove
argument_list|(
name|SimilarityFactory
operator|.
name|CLASS_NAME
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|entry
range|:
name|config
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Object
name|val
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
block|{
name|Element
name|child
init|=
name|doc
operator|.
name|createElement
argument_list|(
name|classToXmlTag
argument_list|(
name|val
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|child
operator|.
name|setAttribute
argument_list|(
name|CommonParams
operator|.
name|NAME
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|child
operator|.
name|setTextContent
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|similarity
operator|.
name|appendChild
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|similarity
return|;
block|}
comment|/** Convert types produced by noggit's ObjectBuilder (Boolean, Double, Long, String) to plugin param XML tags. */
DECL|method|classToXmlTag
specifier|protected
specifier|static
name|String
name|classToXmlTag
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
switch|switch
condition|(
name|clazz
operator|.
name|getSimpleName
argument_list|()
condition|)
block|{
case|case
literal|"Boolean"
case|:
return|return
literal|"bool"
return|;
case|case
literal|"Double"
case|:
return|return
literal|"double"
return|;
case|case
literal|"Long"
case|:
return|return
literal|"long"
return|;
case|case
literal|"String"
case|:
return|return
literal|"str"
return|;
block|}
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Unsupported object type '"
operator|+
name|clazz
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"'"
argument_list|)
throw|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|transformAnalyzer
specifier|protected
specifier|static
name|Element
name|transformAnalyzer
parameter_list|(
name|Document
name|doc
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|json
parameter_list|,
name|String
name|jsonFieldName
parameter_list|,
name|String
name|analyzerType
parameter_list|)
block|{
name|Object
name|jsonField
init|=
name|json
operator|.
name|get
argument_list|(
name|jsonFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|jsonField
operator|==
literal|null
condition|)
return|return
literal|null
return|;
comment|// it's ok for this field to not exist in the JSON map
if|if
condition|(
operator|!
operator|(
name|jsonField
operator|instanceof
name|Map
operator|)
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Invalid fieldType definition! Expected JSON object for "
operator|+
name|jsonFieldName
operator|+
literal|" not a "
operator|+
name|jsonField
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
return|return
name|createAnalyzerElement
argument_list|(
name|doc
argument_list|,
name|analyzerType
argument_list|,
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
operator|)
name|jsonField
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|createAnalyzerElement
specifier|protected
specifier|static
name|Element
name|createAnalyzerElement
parameter_list|(
name|Document
name|doc
parameter_list|,
name|String
name|type
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|analyzer
parameter_list|)
block|{
name|Element
name|analyzerElem
init|=
name|appendAttrs
argument_list|(
name|doc
operator|.
name|createElement
argument_list|(
literal|"analyzer"
argument_list|)
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
name|analyzerElem
operator|.
name|setAttribute
argument_list|(
literal|"type"
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
argument_list|>
name|charFilters
init|=
operator|(
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
argument_list|>
operator|)
name|analyzer
operator|.
name|get
argument_list|(
literal|"charFilters"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|tokenizer
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
operator|)
name|analyzer
operator|.
name|get
argument_list|(
literal|"tokenizer"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
argument_list|>
name|filters
init|=
operator|(
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
argument_list|>
operator|)
name|analyzer
operator|.
name|get
argument_list|(
literal|"filters"
argument_list|)
decl_stmt|;
if|if
condition|(
name|analyzer
operator|.
name|get
argument_list|(
literal|"class"
argument_list|)
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|charFilters
operator|!=
literal|null
condition|)
name|appendFilterElements
argument_list|(
name|doc
argument_list|,
name|analyzerElem
argument_list|,
literal|"charFilter"
argument_list|,
name|charFilters
argument_list|)
expr_stmt|;
if|if
condition|(
name|tokenizer
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Analyzer must define a tokenizer!"
argument_list|)
throw|;
if|if
condition|(
name|tokenizer
operator|.
name|get
argument_list|(
literal|"class"
argument_list|)
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Every tokenizer must define a class property!"
argument_list|)
throw|;
name|analyzerElem
operator|.
name|appendChild
argument_list|(
name|appendAttrs
argument_list|(
name|doc
operator|.
name|createElement
argument_list|(
literal|"tokenizer"
argument_list|)
argument_list|,
name|tokenizer
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|filters
operator|!=
literal|null
condition|)
name|appendFilterElements
argument_list|(
name|doc
argument_list|,
name|analyzerElem
argument_list|,
literal|"filter"
argument_list|,
name|filters
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// When analyzer class is specified: char filters, tokenizers, and filters are disallowed
if|if
condition|(
name|charFilters
operator|!=
literal|null
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"An analyzer with a class property may not define any char filters!"
argument_list|)
throw|;
if|if
condition|(
name|tokenizer
operator|!=
literal|null
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"An analyzer with a class property may not define a tokenizer!"
argument_list|)
throw|;
if|if
condition|(
name|filters
operator|!=
literal|null
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"An analyzer with a class property may not define any filters!"
argument_list|)
throw|;
block|}
return|return
name|analyzerElem
return|;
block|}
DECL|method|appendFilterElements
specifier|protected
specifier|static
name|void
name|appendFilterElements
parameter_list|(
name|Document
name|doc
parameter_list|,
name|Element
name|analyzer
parameter_list|,
name|String
name|filterName
parameter_list|,
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
argument_list|>
name|filters
parameter_list|)
block|{
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|next
range|:
name|filters
control|)
block|{
name|String
name|filterClass
init|=
operator|(
name|String
operator|)
name|next
operator|.
name|get
argument_list|(
literal|"class"
argument_list|)
decl_stmt|;
if|if
condition|(
name|filterClass
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Every "
operator|+
name|filterName
operator|+
literal|" must define a class property!"
argument_list|)
throw|;
name|analyzer
operator|.
name|appendChild
argument_list|(
name|appendAttrs
argument_list|(
name|doc
operator|.
name|createElement
argument_list|(
name|filterName
argument_list|)
argument_list|,
name|next
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|appendAttrs
specifier|protected
specifier|static
name|Element
name|appendAttrs
parameter_list|(
name|Element
name|elm
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|json
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|entry
range|:
name|json
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Object
name|val
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
operator|&&
operator|!
operator|(
name|val
operator|instanceof
name|Map
operator|)
condition|)
name|elm
operator|.
name|setAttribute
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|val
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|elm
return|;
block|}
block|}
end_class

end_unit

