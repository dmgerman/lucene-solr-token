begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.mockfile
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|mockfile
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
name|FilterOutputStream
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**    * A {@code FilterOutputStream2} contains another   * {@code OutputStream}, which it uses as its basic   * source of data, possibly transforming the data along the   * way or providing additional functionality.   *<p>  * Note: unlike {@link FilterOutputStream} this class  * delegates every method by default. This means to transform  * {@code write} calls, you need to override multiple methods.  * On the other hand, it is less trappy: a simple implementation   * that just overrides {@code close} will not force bytes to be   * written one-at-a-time.  */
end_comment

begin_class
DECL|class|FilterOutputStream2
specifier|public
class|class
name|FilterOutputStream2
extends|extends
name|OutputStream
block|{
comment|/**     * The underlying {@code OutputStream} instance.     */
DECL|field|delegate
specifier|protected
specifier|final
name|OutputStream
name|delegate
decl_stmt|;
comment|/**    * Construct a {@code FilterOutputStream2} based on     * the specified base stream.    *<p>    * Note that base stream is closed if this stream is closed.    * @param delegate specified base stream.    */
DECL|method|FilterOutputStream2
specifier|public
name|FilterOutputStream2
parameter_list|(
name|OutputStream
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|write
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

