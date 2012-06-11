begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.enhancements.association
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|enhancements
operator|.
name|association
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
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenStream
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
name|facet
operator|.
name|enhancements
operator|.
name|CategoryEnhancement
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
name|facet
operator|.
name|enhancements
operator|.
name|params
operator|.
name|EnhancementsIndexingParams
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
name|facet
operator|.
name|index
operator|.
name|CategoryListPayloadStream
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
name|facet
operator|.
name|index
operator|.
name|attributes
operator|.
name|OrdinalProperty
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
name|facet
operator|.
name|index
operator|.
name|streaming
operator|.
name|CategoryListTokenizer
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
name|util
operator|.
name|encoding
operator|.
name|SimpleIntEncoder
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Tokenizer for associations of a category  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|AssociationListTokenizer
specifier|public
class|class
name|AssociationListTokenizer
extends|extends
name|CategoryListTokenizer
block|{
DECL|field|payloadStream
specifier|protected
name|CategoryListPayloadStream
name|payloadStream
decl_stmt|;
DECL|field|categoryListTermText
specifier|private
name|String
name|categoryListTermText
decl_stmt|;
DECL|method|AssociationListTokenizer
specifier|public
name|AssociationListTokenizer
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|EnhancementsIndexingParams
name|indexingParams
parameter_list|,
name|CategoryEnhancement
name|enhancement
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|,
name|indexingParams
argument_list|)
expr_stmt|;
name|categoryListTermText
operator|=
name|enhancement
operator|.
name|getCategoryListTermText
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handleStartOfInput
specifier|protected
name|void
name|handleStartOfInput
parameter_list|()
throws|throws
name|IOException
block|{
name|payloadStream
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
name|categoryAttribute
operator|!=
literal|null
condition|)
block|{
name|AssociationProperty
name|associationProperty
init|=
name|AssociationEnhancement
operator|.
name|getAssociationProperty
argument_list|(
name|categoryAttribute
argument_list|)
decl_stmt|;
if|if
condition|(
name|associationProperty
operator|!=
literal|null
operator|&&
name|associationProperty
operator|.
name|hasBeenSet
argument_list|()
condition|)
block|{
name|OrdinalProperty
name|ordinalProperty
init|=
operator|(
name|OrdinalProperty
operator|)
name|categoryAttribute
operator|.
name|getProperty
argument_list|(
name|OrdinalProperty
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|ordinalProperty
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error: Association without ordinal"
argument_list|)
throw|;
block|}
if|if
condition|(
name|payloadStream
operator|==
literal|null
condition|)
block|{
name|payloadStream
operator|=
operator|new
name|CategoryListPayloadStream
argument_list|(
operator|new
name|SimpleIntEncoder
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|payloadStream
operator|.
name|appendIntToStream
argument_list|(
name|ordinalProperty
operator|.
name|getOrdinal
argument_list|()
argument_list|)
expr_stmt|;
name|payloadStream
operator|.
name|appendIntToStream
argument_list|(
name|associationProperty
operator|.
name|getAssociation
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
if|if
condition|(
name|payloadStream
operator|!=
literal|null
condition|)
block|{
name|termAttribute
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|categoryListTermText
argument_list|)
expr_stmt|;
name|payload
operator|.
name|bytes
operator|=
name|payloadStream
operator|.
name|convertStreamToByteArray
argument_list|()
expr_stmt|;
name|payload
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|payload
operator|.
name|length
operator|=
name|payload
operator|.
name|bytes
operator|.
name|length
expr_stmt|;
name|payloadAttribute
operator|.
name|setPayload
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|payloadStream
operator|=
literal|null
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

