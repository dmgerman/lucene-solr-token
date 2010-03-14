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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|Hash
import|;
end_import

begin_class
DECL|class|Lookup3Signature
specifier|public
class|class
name|Lookup3Signature
extends|extends
name|Signature
block|{
DECL|field|hash
specifier|protected
name|long
name|hash
decl_stmt|;
DECL|method|Lookup3Signature
specifier|public
name|Lookup3Signature
parameter_list|()
block|{   }
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|String
name|content
parameter_list|)
block|{
name|hash
operator|=
name|Hash
operator|.
name|lookup3ycs64
argument_list|(
name|content
argument_list|,
literal|0
argument_list|,
name|content
operator|.
name|length
argument_list|()
argument_list|,
name|hash
argument_list|)
expr_stmt|;
block|}
DECL|method|getSignature
specifier|public
name|byte
index|[]
name|getSignature
parameter_list|()
block|{
return|return
operator|new
name|byte
index|[]
block|{
call|(
name|byte
call|)
argument_list|(
name|hash
operator|>>
literal|56
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
name|hash
operator|>>
literal|48
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
name|hash
operator|>>
literal|40
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
name|hash
operator|>>
literal|32
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
name|hash
operator|>>
literal|24
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
name|hash
operator|>>
literal|16
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
name|hash
operator|>>
literal|8
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
name|hash
operator|>>
literal|0
argument_list|)
block|}
return|;
block|}
block|}
end_class

end_unit

