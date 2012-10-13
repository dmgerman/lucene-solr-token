begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.uima.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|uima
operator|.
name|processor
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrInputDocument
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
name|uima
operator|.
name|processor
operator|.
name|SolrUIMAConfiguration
operator|.
name|MapField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|cas
operator|.
name|FSIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|cas
operator|.
name|FeatureStructure
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|cas
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|jcas
operator|.
name|JCas
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|jcas
operator|.
name|tcas
operator|.
name|Annotation
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

begin_comment
comment|/**  * Map UIMA types and features over fields of a Solr document  *   *  */
end_comment

begin_class
DECL|class|UIMAToSolrMapper
specifier|public
class|class
name|UIMAToSolrMapper
block|{
DECL|field|log
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|UIMAToSolrMapper
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|document
specifier|private
specifier|final
name|SolrInputDocument
name|document
decl_stmt|;
DECL|field|cas
specifier|private
specifier|final
name|JCas
name|cas
decl_stmt|;
DECL|method|UIMAToSolrMapper
specifier|public
name|UIMAToSolrMapper
parameter_list|(
name|SolrInputDocument
name|document
parameter_list|,
name|JCas
name|cas
parameter_list|)
block|{
name|this
operator|.
name|document
operator|=
name|document
expr_stmt|;
name|this
operator|.
name|cas
operator|=
name|cas
expr_stmt|;
block|}
comment|/**    * map features of a certain UIMA type to corresponding Solr fields based on the mapping    *    * @param typeName             name of UIMA type to map    */
DECL|method|map
name|void
name|map
parameter_list|(
name|String
name|typeName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|MapField
argument_list|>
name|featureFieldsmapping
parameter_list|)
throws|throws
name|FieldMappingException
block|{
try|try
block|{
name|Type
name|type
init|=
name|cas
operator|.
name|getTypeSystem
argument_list|()
operator|.
name|getType
argument_list|(
name|typeName
argument_list|)
decl_stmt|;
for|for
control|(
name|FSIterator
argument_list|<
name|FeatureStructure
argument_list|>
name|iterator
init|=
name|cas
operator|.
name|getFSIndexRepository
argument_list|()
operator|.
name|getAllIndexedFS
argument_list|(
name|type
argument_list|)
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|FeatureStructure
name|fs
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|featureName
range|:
name|featureFieldsmapping
operator|.
name|keySet
argument_list|()
control|)
block|{
name|MapField
name|mapField
init|=
name|featureFieldsmapping
operator|.
name|get
argument_list|(
name|featureName
argument_list|)
decl_stmt|;
name|String
name|fieldNameFeature
init|=
name|mapField
operator|.
name|getFieldNameFeature
argument_list|()
decl_stmt|;
name|String
name|fieldNameFeatureValue
init|=
name|fieldNameFeature
operator|==
literal|null
condition|?
literal|null
else|:
name|fs
operator|.
name|getFeatureValueAsString
argument_list|(
name|type
operator|.
name|getFeatureByBaseName
argument_list|(
name|fieldNameFeature
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|fieldName
init|=
name|mapField
operator|.
name|getFieldName
argument_list|(
name|fieldNameFeatureValue
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
operator|new
name|StringBuilder
argument_list|(
literal|"mapping "
argument_list|)
operator|.
name|append
argument_list|(
name|typeName
argument_list|)
operator|.
name|append
argument_list|(
literal|"@"
argument_list|)
operator|.
name|append
argument_list|(
name|featureName
argument_list|)
operator|.
name|append
argument_list|(
literal|" to "
argument_list|)
operator|.
name|append
argument_list|(
name|fieldName
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|featureValue
decl_stmt|;
if|if
condition|(
name|fs
operator|instanceof
name|Annotation
operator|&&
literal|"coveredText"
operator|.
name|equals
argument_list|(
name|featureName
argument_list|)
condition|)
block|{
name|featureValue
operator|=
operator|(
operator|(
name|Annotation
operator|)
name|fs
operator|)
operator|.
name|getCoveredText
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|featureValue
operator|=
name|fs
operator|.
name|getFeatureValueAsString
argument_list|(
name|type
operator|.
name|getFeatureByBaseName
argument_list|(
name|featureName
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
operator|new
name|StringBuilder
argument_list|(
literal|"writing "
argument_list|)
operator|.
name|append
argument_list|(
name|featureValue
argument_list|)
operator|.
name|append
argument_list|(
literal|" in "
argument_list|)
operator|.
name|append
argument_list|(
name|fieldName
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|document
operator|.
name|addField
argument_list|(
name|fieldName
argument_list|,
name|featureValue
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|FieldMappingException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

