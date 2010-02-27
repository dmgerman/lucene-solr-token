begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
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

begin_comment
comment|/**  * An average, best guess, MemoryModel that should work okay on most systems.  *   */
end_comment

begin_class
DECL|class|AverageGuessMemoryModel
specifier|public
class|class
name|AverageGuessMemoryModel
extends|extends
name|MemoryModel
block|{
comment|// best guess primitive sizes
DECL|field|sizes
specifier|private
specifier|final
name|Map
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Integer
argument_list|>
name|sizes
init|=
operator|new
name|IdentityHashMap
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
name|boolean
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|byte
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|char
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|short
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|int
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|float
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|double
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|long
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
empty_stmt|;
comment|/*    * (non-Javadoc)    *     * @see org.apache.lucene.util.MemoryModel#getArraySize()    */
annotation|@
name|Override
DECL|method|getArraySize
specifier|public
name|int
name|getArraySize
parameter_list|()
block|{
return|return
literal|16
return|;
block|}
comment|/*    * (non-Javadoc)    *     * @see org.apache.lucene.util.MemoryModel#getClassSize()    */
annotation|@
name|Override
DECL|method|getClassSize
specifier|public
name|int
name|getClassSize
parameter_list|()
block|{
return|return
literal|8
return|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.lucene.util.MemoryModel#getPrimitiveSize(java.lang.Class)    */
annotation|@
name|Override
DECL|method|getPrimitiveSize
specifier|public
name|int
name|getPrimitiveSize
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
return|return
name|sizes
operator|.
name|get
argument_list|(
name|clazz
argument_list|)
operator|.
name|intValue
argument_list|()
return|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.lucene.util.MemoryModel#getReferenceSize()    */
annotation|@
name|Override
DECL|method|getReferenceSize
specifier|public
name|int
name|getReferenceSize
parameter_list|()
block|{
return|return
literal|4
return|;
block|}
block|}
end_class

end_unit

