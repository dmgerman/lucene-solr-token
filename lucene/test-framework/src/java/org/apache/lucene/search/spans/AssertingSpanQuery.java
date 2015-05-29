begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
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
name|index
operator|.
name|IndexReader
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
name|search
operator|.
name|IndexSearcher
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
name|search
operator|.
name|Query
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

begin_comment
comment|/** Wraps a span query with asserts */
end_comment

begin_class
DECL|class|AssertingSpanQuery
specifier|public
class|class
name|AssertingSpanQuery
extends|extends
name|SpanQuery
block|{
DECL|field|in
specifier|private
specifier|final
name|SpanQuery
name|in
decl_stmt|;
DECL|method|AssertingSpanQuery
specifier|public
name|AssertingSpanQuery
parameter_list|(
name|SpanQuery
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|in
operator|.
name|getField
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|"AssertingSpanQuery("
operator|+
name|in
operator|.
name|toString
argument_list|(
name|field
argument_list|)
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|SpanWeight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|,
name|SpanCollectorFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|SpanWeight
name|weight
init|=
name|in
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
name|needsScores
argument_list|,
name|factory
argument_list|)
decl_stmt|;
return|return
operator|new
name|AssertingSpanWeight
argument_list|(
name|searcher
argument_list|,
name|weight
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setBoost
specifier|public
name|void
name|setBoost
parameter_list|(
name|float
name|b
parameter_list|)
block|{
name|in
operator|.
name|setBoost
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getBoost
specifier|public
name|float
name|getBoost
parameter_list|()
block|{
return|return
name|in
operator|.
name|getBoost
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|q
init|=
name|in
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|q
operator|==
name|in
condition|)
block|{
return|return
name|this
return|;
block|}
elseif|else
if|if
condition|(
name|q
operator|instanceof
name|SpanQuery
condition|)
block|{
return|return
operator|new
name|AssertingSpanQuery
argument_list|(
operator|(
name|SpanQuery
operator|)
name|q
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|q
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Query
name|clone
parameter_list|()
block|{
return|return
operator|new
name|AssertingSpanQuery
argument_list|(
operator|(
name|SpanQuery
operator|)
name|in
operator|.
name|clone
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|in
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|in
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|AssertingSpanQuery
name|other
init|=
operator|(
name|AssertingSpanQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|in
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|in
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|in
operator|.
name|equals
argument_list|(
name|other
operator|.
name|in
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

