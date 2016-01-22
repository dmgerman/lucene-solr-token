begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|util
operator|.
name|ArrayList
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
name|StringHelper
import|;
end_import

begin_class
DECL|class|MultiPointValues
class|class
name|MultiPointValues
extends|extends
name|PointValues
block|{
DECL|field|subs
specifier|private
specifier|final
name|List
argument_list|<
name|PointValues
argument_list|>
name|subs
decl_stmt|;
DECL|field|docBases
specifier|private
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|docBases
decl_stmt|;
DECL|method|MultiPointValues
specifier|private
name|MultiPointValues
parameter_list|(
name|List
argument_list|<
name|PointValues
argument_list|>
name|subs
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|docBases
parameter_list|)
block|{
name|this
operator|.
name|subs
operator|=
name|subs
expr_stmt|;
name|this
operator|.
name|docBases
operator|=
name|docBases
expr_stmt|;
block|}
DECL|method|get
specifier|public
specifier|static
name|PointValues
name|get
parameter_list|(
name|IndexReader
name|r
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|r
operator|.
name|leaves
argument_list|()
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|leaves
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|size
operator|==
literal|1
condition|)
block|{
return|return
name|leaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|getPointValues
argument_list|()
return|;
block|}
name|List
argument_list|<
name|PointValues
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|docBases
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|LeafReaderContext
name|context
init|=
name|leaves
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|PointValues
name|v
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getPointValues
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
name|docBases
operator|.
name|add
argument_list|(
name|context
operator|.
name|docBase
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|values
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|MultiPointValues
argument_list|(
name|values
argument_list|,
name|docBases
argument_list|)
return|;
block|}
comment|/** Finds all documents and points matching the provided visitor */
DECL|method|intersect
specifier|public
name|void
name|intersect
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|IntersectVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|int
name|docBase
init|=
name|docBases
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|subs
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|intersect
argument_list|(
name|fieldName
argument_list|,
operator|new
name|IntersectVisitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|visitor
operator|.
name|visit
argument_list|(
name|docBase
operator|+
name|docID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|,
name|byte
index|[]
name|packedValue
parameter_list|)
throws|throws
name|IOException
block|{
name|visitor
operator|.
name|visit
argument_list|(
name|docBase
operator|+
name|docID
argument_list|,
name|packedValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Relation
name|compare
parameter_list|(
name|byte
index|[]
name|minPackedValue
parameter_list|,
name|byte
index|[]
name|maxPackedValue
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|compare
argument_list|(
name|minPackedValue
argument_list|,
name|maxPackedValue
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"MultiPointValues("
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|"docBase="
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|docBases
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|" sub="
operator|+
name|subs
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getMinPackedValue
specifier|public
name|byte
index|[]
name|getMinPackedValue
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|result
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|byte
index|[]
name|minPackedValue
init|=
name|subs
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getMinPackedValue
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|minPackedValue
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|minPackedValue
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|int
name|numDims
init|=
name|subs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getNumDimensions
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|int
name|bytesPerDim
init|=
name|subs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBytesPerDimension
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|dim
init|=
literal|0
init|;
name|dim
operator|<
name|numDims
condition|;
name|dim
operator|++
control|)
block|{
name|int
name|offset
init|=
name|dim
operator|*
name|bytesPerDim
decl_stmt|;
if|if
condition|(
name|StringHelper
operator|.
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|minPackedValue
argument_list|,
name|offset
argument_list|,
name|result
argument_list|,
name|offset
argument_list|)
operator|<
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|minPackedValue
argument_list|,
name|offset
argument_list|,
name|result
argument_list|,
name|offset
argument_list|,
name|bytesPerDim
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|getMaxPackedValue
specifier|public
name|byte
index|[]
name|getMaxPackedValue
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|result
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|byte
index|[]
name|maxPackedValue
init|=
name|subs
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getMaxPackedValue
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|maxPackedValue
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|maxPackedValue
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|int
name|numDims
init|=
name|subs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getNumDimensions
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|int
name|bytesPerDim
init|=
name|subs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBytesPerDimension
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|dim
init|=
literal|0
init|;
name|dim
operator|<
name|numDims
condition|;
name|dim
operator|++
control|)
block|{
name|int
name|offset
init|=
name|dim
operator|*
name|bytesPerDim
decl_stmt|;
if|if
condition|(
name|StringHelper
operator|.
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|maxPackedValue
argument_list|,
name|offset
argument_list|,
name|result
argument_list|,
name|offset
argument_list|)
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|maxPackedValue
argument_list|,
name|offset
argument_list|,
name|result
argument_list|,
name|offset
argument_list|,
name|bytesPerDim
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|getNumDimensions
specifier|public
name|int
name|getNumDimensions
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|int
name|result
init|=
name|subs
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getNumDimensions
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|0
condition|)
block|{
return|return
name|result
return|;
block|}
block|}
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getBytesPerDimension
specifier|public
name|int
name|getBytesPerDimension
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|int
name|result
init|=
name|subs
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getBytesPerDimension
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|0
condition|)
block|{
return|return
name|result
return|;
block|}
block|}
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit
