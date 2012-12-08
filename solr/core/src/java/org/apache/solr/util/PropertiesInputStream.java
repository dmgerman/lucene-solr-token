begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
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
name|EOFException
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
name|InputStream
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
name|store
operator|.
name|IndexInput
import|;
end_import

begin_class
DECL|class|PropertiesInputStream
specifier|public
class|class
name|PropertiesInputStream
extends|extends
name|InputStream
block|{
DECL|field|is
specifier|private
name|IndexInput
name|is
decl_stmt|;
DECL|method|PropertiesInputStream
specifier|public
name|PropertiesInputStream
parameter_list|(
name|IndexInput
name|is
parameter_list|)
block|{
name|this
operator|.
name|is
operator|=
name|is
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
name|next
decl_stmt|;
try|try
block|{
name|next
operator|=
name|is
operator|.
name|readByte
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
name|next
return|;
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
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

