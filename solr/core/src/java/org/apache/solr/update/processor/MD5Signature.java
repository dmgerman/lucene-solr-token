begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
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
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
DECL|class|MD5Signature
specifier|public
class|class
name|MD5Signature
extends|extends
name|Signature
block|{
DECL|field|log
specifier|protected
specifier|final
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MD5Signature
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DIGESTER_FACTORY
specifier|private
specifier|static
name|ThreadLocal
argument_list|<
name|MessageDigest
argument_list|>
name|DIGESTER_FACTORY
init|=
operator|new
name|ThreadLocal
argument_list|<
name|MessageDigest
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|MessageDigest
name|initialValue
parameter_list|()
block|{
try|try
block|{
return|return
name|MessageDigest
operator|.
name|getInstance
argument_list|(
literal|"MD5"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
decl_stmt|;
DECL|field|digester
specifier|private
name|MessageDigest
name|digester
decl_stmt|;
DECL|method|MD5Signature
specifier|public
name|MD5Signature
parameter_list|()
block|{
name|digester
operator|=
name|DIGESTER_FACTORY
operator|.
name|get
argument_list|()
expr_stmt|;
name|digester
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|String
name|content
parameter_list|)
block|{
try|try
block|{
name|digester
operator|.
name|update
argument_list|(
name|content
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
comment|// won't happen
name|log
operator|.
name|error
argument_list|(
literal|"UTF-8 not supported"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getSignature
specifier|public
name|byte
index|[]
name|getSignature
parameter_list|()
block|{
return|return
name|digester
operator|.
name|digest
argument_list|()
return|;
block|}
block|}
end_class

end_unit

