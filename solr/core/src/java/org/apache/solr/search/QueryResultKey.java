begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Sort
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
name|SortField
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/** A hash key encapsulating a query, a list of filters, and a sort  *  */
end_comment

begin_class
DECL|class|QueryResultKey
specifier|public
specifier|final
class|class
name|QueryResultKey
block|{
DECL|field|query
specifier|final
name|Query
name|query
decl_stmt|;
DECL|field|sort
specifier|final
name|Sort
name|sort
decl_stmt|;
DECL|field|sfields
specifier|final
name|SortField
index|[]
name|sfields
decl_stmt|;
DECL|field|filters
specifier|final
name|List
argument_list|<
name|Query
argument_list|>
name|filters
decl_stmt|;
DECL|field|nc_flags
specifier|final
name|int
name|nc_flags
decl_stmt|;
comment|// non-comparable flags... ignored by hashCode and equals
DECL|field|hc
specifier|private
specifier|final
name|int
name|hc
decl_stmt|;
comment|// cached hashCode
DECL|field|defaultSort
specifier|private
specifier|static
name|SortField
index|[]
name|defaultSort
init|=
operator|new
name|SortField
index|[
literal|0
index|]
decl_stmt|;
DECL|method|QueryResultKey
specifier|public
name|QueryResultKey
parameter_list|(
name|Query
name|query
parameter_list|,
name|List
argument_list|<
name|Query
argument_list|>
name|filters
parameter_list|,
name|Sort
name|sort
parameter_list|,
name|int
name|nc_flags
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|sort
operator|=
name|sort
expr_stmt|;
name|this
operator|.
name|filters
operator|=
name|filters
expr_stmt|;
name|this
operator|.
name|nc_flags
operator|=
name|nc_flags
expr_stmt|;
name|int
name|h
init|=
name|query
operator|.
name|hashCode
argument_list|()
decl_stmt|;
if|if
condition|(
name|filters
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Query
name|filt
range|:
name|filters
control|)
name|h
operator|+=
name|filt
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
name|sfields
operator|=
operator|(
name|this
operator|.
name|sort
operator|!=
literal|null
operator|)
condition|?
name|this
operator|.
name|sort
operator|.
name|getSort
argument_list|()
else|:
name|defaultSort
expr_stmt|;
for|for
control|(
name|SortField
name|sf
range|:
name|sfields
control|)
block|{
name|h
operator|=
name|h
operator|*
literal|29
operator|+
name|sf
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
name|hc
operator|=
name|h
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|hc
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
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
name|this
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|QueryResultKey
operator|)
condition|)
return|return
literal|false
return|;
name|QueryResultKey
name|other
init|=
operator|(
name|QueryResultKey
operator|)
name|o
decl_stmt|;
comment|// fast check of the whole hash code... most hash tables will only use
comment|// some of the bits, so if this is a hash collision, it's still likely
comment|// that the full cached hash code will be different.
if|if
condition|(
name|this
operator|.
name|hc
operator|!=
name|other
operator|.
name|hc
condition|)
return|return
literal|false
return|;
comment|// check for the thing most likely to be different (and the fastest things)
comment|// first.
if|if
condition|(
name|this
operator|.
name|sfields
operator|.
name|length
operator|!=
name|other
operator|.
name|sfields
operator|.
name|length
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|this
operator|.
name|query
operator|.
name|equals
argument_list|(
name|other
operator|.
name|query
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|isEqual
argument_list|(
name|this
operator|.
name|filters
argument_list|,
name|other
operator|.
name|filters
argument_list|)
condition|)
return|return
literal|false
return|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sfields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SortField
name|sf1
init|=
name|this
operator|.
name|sfields
index|[
name|i
index|]
decl_stmt|;
name|SortField
name|sf2
init|=
name|other
operator|.
name|sfields
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|sf1
operator|.
name|equals
argument_list|(
name|sf2
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|// Do fast version, expecting that filters are ordered and only
comment|// fall back to unordered compare on the first non-equal elements.
comment|// This will only be called if the hash code of the entire key already
comment|// matched, so the slower unorderedCompare should pretty much never
comment|// be called if filter lists are generally ordered.
DECL|method|isEqual
specifier|private
specifier|static
name|boolean
name|isEqual
parameter_list|(
name|List
argument_list|<
name|Query
argument_list|>
name|fqList1
parameter_list|,
name|List
argument_list|<
name|Query
argument_list|>
name|fqList2
parameter_list|)
block|{
if|if
condition|(
name|fqList1
operator|==
name|fqList2
condition|)
return|return
literal|true
return|;
comment|// takes care of identity and null cases
if|if
condition|(
name|fqList1
operator|==
literal|null
operator|||
name|fqList2
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|int
name|sz
init|=
name|fqList1
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|sz
operator|!=
name|fqList2
operator|.
name|size
argument_list|()
condition|)
return|return
literal|false
return|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sz
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|fqList1
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|equals
argument_list|(
name|fqList2
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|unorderedCompare
argument_list|(
name|fqList1
argument_list|,
name|fqList2
argument_list|,
name|i
argument_list|)
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|unorderedCompare
specifier|private
specifier|static
name|boolean
name|unorderedCompare
parameter_list|(
name|List
argument_list|<
name|Query
argument_list|>
name|fqList1
parameter_list|,
name|List
argument_list|<
name|Query
argument_list|>
name|fqList2
parameter_list|,
name|int
name|start
parameter_list|)
block|{
name|int
name|sz
init|=
name|fqList1
operator|.
name|size
argument_list|()
decl_stmt|;
name|outer
label|:
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|Query
name|q1
init|=
name|fqList1
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|start
init|;
name|j
operator|<
name|sz
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|q1
operator|.
name|equals
argument_list|(
name|fqList2
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|)
condition|)
continue|continue
name|outer
continue|;
block|}
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

