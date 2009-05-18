begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
package|;
end_package

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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Used by MockRAMDirectory to create an input stream that  * keeps track of when it's been closed.  */
end_comment

begin_class
DECL|class|MockRAMInputStream
specifier|public
class|class
name|MockRAMInputStream
extends|extends
name|RAMInputStream
block|{
DECL|field|dir
specifier|private
name|MockRAMDirectory
name|dir
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|isClone
specifier|private
name|boolean
name|isClone
decl_stmt|;
comment|/** Construct an empty output buffer.     * @throws IOException */
DECL|method|MockRAMInputStream
specifier|public
name|MockRAMInputStream
parameter_list|(
name|MockRAMDirectory
name|dir
parameter_list|,
name|String
name|name
parameter_list|,
name|RAMFile
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Pending resolution on LUCENE-686 we may want to
comment|// remove the conditional check so we also track that
comment|// all clones get closed:
if|if
condition|(
operator|!
name|isClone
condition|)
block|{
synchronized|synchronized
init|(
name|dir
init|)
block|{
name|Integer
name|v
init|=
operator|(
name|Integer
operator|)
name|dir
operator|.
name|openFiles
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
comment|// Could be null when MockRAMDirectory.crash() was called
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|v
operator|.
name|intValue
argument_list|()
operator|==
literal|1
condition|)
block|{
name|dir
operator|.
name|openFiles
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|v
operator|=
operator|new
name|Integer
argument_list|(
name|v
operator|.
name|intValue
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|dir
operator|.
name|openFiles
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|MockRAMInputStream
name|clone
init|=
operator|(
name|MockRAMInputStream
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|isClone
operator|=
literal|true
expr_stmt|;
comment|// Pending resolution on LUCENE-686 we may want to
comment|// uncomment this code so that we also track that all
comment|// clones get closed:
comment|/*     synchronized(dir.openFiles) {       if (dir.openFiles.containsKey(name)) {         Integer v = (Integer) dir.openFiles.get(name);         v = new Integer(v.intValue()+1);         dir.openFiles.put(name, v);       } else {         throw new RuntimeException("BUG: cloned file was not open?");       }     }     */
return|return
name|clone
return|;
block|}
block|}
end_class

end_unit

