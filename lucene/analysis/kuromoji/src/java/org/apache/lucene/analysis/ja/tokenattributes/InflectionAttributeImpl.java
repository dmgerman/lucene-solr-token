begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.ja.tokenattributes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ja
operator|.
name|tokenattributes
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|ja
operator|.
name|Token
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
name|ja
operator|.
name|util
operator|.
name|ToStringUtil
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
name|AttributeImpl
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
name|AttributeReflector
import|;
end_import

begin_comment
comment|/**  * Attribute for Kuromoji inflection data.  */
end_comment

begin_class
DECL|class|InflectionAttributeImpl
specifier|public
class|class
name|InflectionAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|InflectionAttribute
implements|,
name|Cloneable
block|{
DECL|field|token
specifier|private
name|Token
name|token
decl_stmt|;
DECL|method|getInflectionType
specifier|public
name|String
name|getInflectionType
parameter_list|()
block|{
return|return
name|token
operator|==
literal|null
condition|?
literal|null
else|:
name|token
operator|.
name|getInflectionType
argument_list|()
return|;
block|}
DECL|method|getInflectionForm
specifier|public
name|String
name|getInflectionForm
parameter_list|()
block|{
return|return
name|token
operator|==
literal|null
condition|?
literal|null
else|:
name|token
operator|.
name|getInflectionForm
argument_list|()
return|;
block|}
DECL|method|setToken
specifier|public
name|void
name|setToken
parameter_list|(
name|Token
name|token
parameter_list|)
block|{
name|this
operator|.
name|token
operator|=
name|token
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|token
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{
name|InflectionAttribute
name|t
init|=
operator|(
name|InflectionAttribute
operator|)
name|target
decl_stmt|;
name|t
operator|.
name|setToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reflectWith
specifier|public
name|void
name|reflectWith
parameter_list|(
name|AttributeReflector
name|reflector
parameter_list|)
block|{
name|String
name|type
init|=
name|getInflectionType
argument_list|()
decl_stmt|;
name|String
name|typeEN
init|=
name|type
operator|==
literal|null
condition|?
literal|null
else|:
name|ToStringUtil
operator|.
name|getInflectionTypeTranslation
argument_list|(
name|type
argument_list|)
decl_stmt|;
name|reflector
operator|.
name|reflect
argument_list|(
name|InflectionAttribute
operator|.
name|class
argument_list|,
literal|"inflectionType"
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|reflector
operator|.
name|reflect
argument_list|(
name|InflectionAttribute
operator|.
name|class
argument_list|,
literal|"inflectionType (en)"
argument_list|,
name|typeEN
argument_list|)
expr_stmt|;
name|String
name|form
init|=
name|getInflectionForm
argument_list|()
decl_stmt|;
name|String
name|formEN
init|=
name|form
operator|==
literal|null
condition|?
literal|null
else|:
name|ToStringUtil
operator|.
name|getInflectedFormTranslation
argument_list|(
name|form
argument_list|)
decl_stmt|;
name|reflector
operator|.
name|reflect
argument_list|(
name|InflectionAttribute
operator|.
name|class
argument_list|,
literal|"inflectionForm"
argument_list|,
name|form
argument_list|)
expr_stmt|;
name|reflector
operator|.
name|reflect
argument_list|(
name|InflectionAttribute
operator|.
name|class
argument_list|,
literal|"inflectionForm (en)"
argument_list|,
name|formEN
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

