begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Title:        LARM Lanlab Retrieval Machine<p>  * Description:<p>  * Copyright:    Copyright (c)<p>  * Company:<p>  * @author  * @version 1.0  */
end_comment

begin_package
DECL|package|de.lanlab.larm.util
package|package
name|de
operator|.
name|lanlab
operator|.
name|larm
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
name|*
import|;
end_import

begin_class
DECL|class|ObservableInputStream
specifier|public
class|class
name|ObservableInputStream
extends|extends
name|FilterInputStream
block|{
DECL|field|reporting
specifier|private
name|boolean
name|reporting
init|=
literal|true
decl_stmt|;
DECL|field|startTime
specifier|private
name|long
name|startTime
decl_stmt|;
DECL|field|totalRead
specifier|private
name|int
name|totalRead
init|=
literal|0
decl_stmt|;
DECL|field|step
specifier|private
name|int
name|step
init|=
literal|1
decl_stmt|;
DECL|field|nextStep
specifier|private
name|int
name|nextStep
init|=
literal|0
decl_stmt|;
DECL|field|observer
name|InputStreamObserver
name|observer
decl_stmt|;
DECL|method|ObservableInputStream
specifier|public
name|ObservableInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|InputStreamObserver
name|iso
parameter_list|,
name|int
name|reportingStep
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|observer
operator|=
name|iso
expr_stmt|;
name|observer
operator|.
name|notifyOpened
argument_list|(
name|this
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
argument_list|)
expr_stmt|;
name|nextStep
operator|=
name|step
operator|=
name|reportingStep
expr_stmt|;
block|}
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
name|observer
operator|.
name|notifyClosed
argument_list|(
name|this
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
argument_list|)
expr_stmt|;
block|}
DECL|method|setReporting
specifier|public
name|void
name|setReporting
parameter_list|(
name|boolean
name|reporting
parameter_list|)
block|{
name|this
operator|.
name|reporting
operator|=
name|reporting
expr_stmt|;
block|}
DECL|method|isReporting
specifier|public
name|boolean
name|isReporting
parameter_list|()
block|{
return|return
name|reporting
return|;
block|}
DECL|method|setReportingStep
specifier|public
name|void
name|setReportingStep
parameter_list|(
name|int
name|step
parameter_list|)
block|{
name|this
operator|.
name|step
operator|=
name|step
expr_stmt|;
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|readByte
init|=
name|super
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|reporting
condition|)
block|{
name|notifyObserver
argument_list|(
name|readByte
operator|>=
literal|0
condition|?
literal|1
else|:
literal|0
argument_list|)
expr_stmt|;
block|}
return|return
name|readByte
return|;
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|nrRead
init|=
name|super
operator|.
name|read
argument_list|(
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|reporting
condition|)
block|{
name|notifyObserver
argument_list|(
name|nrRead
argument_list|)
expr_stmt|;
block|}
return|return
name|nrRead
return|;
block|}
DECL|method|notifyObserver
specifier|private
name|void
name|notifyObserver
parameter_list|(
name|int
name|nrRead
parameter_list|)
block|{
if|if
condition|(
name|nrRead
operator|>
literal|0
condition|)
block|{
name|totalRead
operator|+=
name|nrRead
expr_stmt|;
if|if
condition|(
name|totalRead
operator|>
name|nextStep
condition|)
block|{
name|nextStep
operator|+=
name|step
expr_stmt|;
name|observer
operator|.
name|notifyRead
argument_list|(
name|this
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
argument_list|,
name|nrRead
argument_list|,
name|totalRead
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|observer
operator|.
name|notifyFinished
argument_list|(
name|this
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
argument_list|,
name|totalRead
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offs
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|nrRead
init|=
name|super
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|offs
argument_list|,
name|size
argument_list|)
decl_stmt|;
if|if
condition|(
name|reporting
condition|)
block|{
name|notifyObserver
argument_list|(
name|nrRead
argument_list|)
expr_stmt|;
block|}
return|return
name|nrRead
return|;
block|}
block|}
end_class

end_unit

