begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  Licensed to the Apache Software Foundation (ASF) under one or more  *  contributor license agreements.  See the NOTICE file distributed with  *  this work for additional information regarding copyright ownership.  *  The ASF licenses this file to You under the Apache License, Version 2.0  *  (the "License"); you may not use this file except in compliance with  *  the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  *  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|BuildException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|types
operator|.
name|Parameter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|types
operator|.
name|selectors
operator|.
name|BaseExtendSelector
import|;
end_import

begin_comment
comment|/** Divides filesets into equal groups */
end_comment

begin_class
DECL|class|LuceneJUnitDividingSelector
specifier|public
class|class
name|LuceneJUnitDividingSelector
extends|extends
name|BaseExtendSelector
block|{
DECL|field|counter
specifier|private
name|int
name|counter
decl_stmt|;
comment|/** Number of total parts to split. */
DECL|field|divisor
specifier|private
name|int
name|divisor
decl_stmt|;
comment|/** Current part to accept. */
DECL|field|part
specifier|private
name|int
name|part
decl_stmt|;
DECL|method|setParameters
specifier|public
name|void
name|setParameters
parameter_list|(
name|Parameter
index|[]
name|pParameters
parameter_list|)
block|{
name|super
operator|.
name|setParameters
argument_list|(
name|pParameters
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|pParameters
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|Parameter
name|p
init|=
name|pParameters
index|[
name|j
index|]
decl_stmt|;
if|if
condition|(
literal|"divisor"
operator|.
name|equalsIgnoreCase
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|divisor
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|p
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"part"
operator|.
name|equalsIgnoreCase
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|part
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|p
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"unknown "
operator|+
name|p
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|verifySettings
specifier|public
name|void
name|verifySettings
parameter_list|()
block|{
name|super
operator|.
name|verifySettings
argument_list|()
expr_stmt|;
if|if
condition|(
name|divisor
operator|<=
literal|0
operator|||
name|part
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"part or divisor not set"
argument_list|)
throw|;
block|}
if|if
condition|(
name|part
operator|>
name|divisor
condition|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"part must be<= divisor"
argument_list|)
throw|;
block|}
block|}
DECL|method|isSelected
specifier|public
name|boolean
name|isSelected
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|name
parameter_list|,
name|File
name|path
parameter_list|)
block|{
name|counter
operator|=
name|counter
operator|%
name|divisor
operator|+
literal|1
expr_stmt|;
return|return
name|counter
operator|==
name|part
return|;
block|}
block|}
end_class

end_unit

