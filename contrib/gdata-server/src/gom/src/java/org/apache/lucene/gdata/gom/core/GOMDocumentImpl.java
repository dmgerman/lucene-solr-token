begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.gom.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|gom
operator|.
name|core
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
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
name|gdata
operator|.
name|gom
operator|.
name|GOMDocument
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
name|gdata
operator|.
name|gom
operator|.
name|GOMElement
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
name|gdata
operator|.
name|gom
operator|.
name|writer
operator|.
name|GOMOutputWriter
import|;
end_import

begin_comment
comment|/**  * @author Simon Willnauer  * @param<T>  */
end_comment

begin_class
DECL|class|GOMDocumentImpl
specifier|public
class|class
name|GOMDocumentImpl
parameter_list|<
name|T
extends|extends
name|GOMElement
parameter_list|>
implements|implements
name|GOMDocument
argument_list|<
name|T
argument_list|>
block|{
DECL|field|DEFAULT_ENCODING
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_ENCODING
init|=
literal|"UTF-8"
decl_stmt|;
DECL|field|DEFAULT_VERSION
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_VERSION
init|=
literal|"1.0"
decl_stmt|;
DECL|field|root
specifier|private
name|T
name|root
decl_stmt|;
DECL|field|version
specifier|private
name|String
name|version
decl_stmt|;
DECL|field|charEncoding
specifier|private
name|String
name|charEncoding
decl_stmt|;
comment|/** 	 *  	 */
DECL|method|GOMDocumentImpl
specifier|public
name|GOMDocumentImpl
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMDocument#setRootElement(org.apache.lucene.gdata.gom.GOMElement) 	 */
DECL|method|setRootElement
specifier|public
name|void
name|setRootElement
parameter_list|(
name|T
name|aRootElement
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|aRootElement
expr_stmt|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMDocument#getRootElement() 	 */
DECL|method|getRootElement
specifier|public
name|T
name|getRootElement
parameter_list|()
block|{
return|return
name|this
operator|.
name|root
return|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMDocument#setVersion(java.lang.String) 	 */
DECL|method|setVersion
specifier|public
name|void
name|setVersion
parameter_list|(
name|String
name|aVersion
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|aVersion
expr_stmt|;
block|}
comment|/** 	 * @return the version 	 * @uml.property name="version" 	 */
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|this
operator|.
name|version
return|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMDocument#getCharacterEncoding() 	 */
DECL|method|getCharacterEncoding
specifier|public
name|String
name|getCharacterEncoding
parameter_list|()
block|{
return|return
name|this
operator|.
name|charEncoding
return|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMDocument#setCharacterEncoding(java.lang.String) 	 */
DECL|method|setCharacterEncoding
specifier|public
name|void
name|setCharacterEncoding
parameter_list|(
name|String
name|aEncoding
parameter_list|)
block|{
name|this
operator|.
name|charEncoding
operator|=
name|aEncoding
expr_stmt|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMDocument#writeAtomOutput(org.apache.lucene.gdata.gom.writer.GOMOutputWriter) 	 */
DECL|method|writeAtomOutput
specifier|public
name|void
name|writeAtomOutput
parameter_list|(
name|GOMOutputWriter
name|aStreamWriter
parameter_list|)
throws|throws
name|XMLStreamException
block|{
try|try
block|{
name|aStreamWriter
operator|.
name|writeStartDocument
argument_list|(
name|this
operator|.
name|charEncoding
operator|==
literal|null
condition|?
name|DEFAULT_ENCODING
else|:
name|this
operator|.
name|charEncoding
argument_list|,
name|this
operator|.
name|version
operator|==
literal|null
condition|?
name|DEFAULT_VERSION
else|:
name|this
operator|.
name|version
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|root
operator|!=
literal|null
condition|)
name|this
operator|.
name|root
operator|.
name|writeAtomOutput
argument_list|(
name|aStreamWriter
argument_list|)
expr_stmt|;
name|aStreamWriter
operator|.
name|writeEndDocument
argument_list|()
expr_stmt|;
name|aStreamWriter
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|aStreamWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMDocument#writeRssOutput(org.apache.lucene.gdata.gom.writer.GOMOutputWriter) 	 */
DECL|method|writeRssOutput
specifier|public
name|void
name|writeRssOutput
parameter_list|(
name|GOMOutputWriter
name|aStreamWriter
parameter_list|)
throws|throws
name|XMLStreamException
block|{
try|try
block|{
name|aStreamWriter
operator|.
name|writeStartDocument
argument_list|(
name|this
operator|.
name|charEncoding
operator|==
literal|null
condition|?
name|DEFAULT_ENCODING
else|:
name|this
operator|.
name|charEncoding
argument_list|,
name|this
operator|.
name|version
operator|==
literal|null
condition|?
name|DEFAULT_VERSION
else|:
name|this
operator|.
name|version
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|root
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|root
operator|.
name|writeRssOutput
argument_list|(
name|aStreamWriter
argument_list|)
expr_stmt|;
block|}
name|aStreamWriter
operator|.
name|writeEndDocument
argument_list|()
expr_stmt|;
name|aStreamWriter
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|aStreamWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

