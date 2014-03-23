begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
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
name|Set
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
name|BytesRef
import|;
end_import

begin_comment
comment|/** corresponds to {@link InputIterator}'s entries */
end_comment

begin_class
DECL|class|Input
specifier|public
specifier|final
class|class
name|Input
block|{
DECL|field|term
specifier|public
specifier|final
name|BytesRef
name|term
decl_stmt|;
DECL|field|v
specifier|public
specifier|final
name|long
name|v
decl_stmt|;
DECL|field|payload
specifier|public
specifier|final
name|BytesRef
name|payload
decl_stmt|;
DECL|field|hasPayloads
specifier|public
specifier|final
name|boolean
name|hasPayloads
decl_stmt|;
DECL|field|contexts
specifier|public
specifier|final
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|contexts
decl_stmt|;
DECL|field|hasContexts
specifier|public
specifier|final
name|boolean
name|hasContexts
decl_stmt|;
DECL|method|Input
specifier|public
name|Input
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|long
name|v
parameter_list|,
name|BytesRef
name|payload
parameter_list|)
block|{
name|this
argument_list|(
name|term
argument_list|,
name|v
argument_list|,
name|payload
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|Input
specifier|public
name|Input
parameter_list|(
name|String
name|term
parameter_list|,
name|long
name|v
parameter_list|,
name|BytesRef
name|payload
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|term
argument_list|)
argument_list|,
name|v
argument_list|,
name|payload
argument_list|)
expr_stmt|;
block|}
DECL|method|Input
specifier|public
name|Input
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|long
name|v
parameter_list|,
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|contexts
parameter_list|)
block|{
name|this
argument_list|(
name|term
argument_list|,
name|v
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|contexts
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|Input
specifier|public
name|Input
parameter_list|(
name|String
name|term
parameter_list|,
name|long
name|v
parameter_list|,
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|contexts
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|term
argument_list|)
argument_list|,
name|v
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|contexts
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|Input
specifier|public
name|Input
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|long
name|v
parameter_list|)
block|{
name|this
argument_list|(
name|term
argument_list|,
name|v
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|Input
specifier|public
name|Input
parameter_list|(
name|String
name|term
parameter_list|,
name|long
name|v
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|term
argument_list|)
argument_list|,
name|v
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|Input
specifier|public
name|Input
parameter_list|(
name|String
name|term
parameter_list|,
name|int
name|v
parameter_list|,
name|BytesRef
name|payload
parameter_list|,
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|contexts
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|term
argument_list|)
argument_list|,
name|v
argument_list|,
name|payload
argument_list|,
literal|true
argument_list|,
name|contexts
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|Input
specifier|public
name|Input
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|long
name|v
parameter_list|,
name|BytesRef
name|payload
parameter_list|,
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|contexts
parameter_list|)
block|{
name|this
argument_list|(
name|term
argument_list|,
name|v
argument_list|,
name|payload
argument_list|,
literal|true
argument_list|,
name|contexts
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|Input
specifier|public
name|Input
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|long
name|v
parameter_list|,
name|BytesRef
name|payload
parameter_list|,
name|boolean
name|hasPayloads
parameter_list|,
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|contexts
parameter_list|,
name|boolean
name|hasContexts
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
name|this
operator|.
name|v
operator|=
name|v
expr_stmt|;
name|this
operator|.
name|payload
operator|=
name|payload
expr_stmt|;
name|this
operator|.
name|hasPayloads
operator|=
name|hasPayloads
expr_stmt|;
name|this
operator|.
name|contexts
operator|=
name|contexts
expr_stmt|;
name|this
operator|.
name|hasContexts
operator|=
name|hasContexts
expr_stmt|;
block|}
DECL|method|hasContexts
specifier|public
name|boolean
name|hasContexts
parameter_list|()
block|{
return|return
name|hasContexts
return|;
block|}
DECL|method|hasPayloads
specifier|public
name|boolean
name|hasPayloads
parameter_list|()
block|{
return|return
name|hasPayloads
return|;
block|}
block|}
end_class

end_unit

