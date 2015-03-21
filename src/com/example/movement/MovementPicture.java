package com.example.movement;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.R.integer;
import android.opengl.GLES20;

import com.example.forpublicuse.Constant;
import com.example.forpublicuse.MatrixState;
import com.example.forpublicuse.ShaderUtil;
import com.example.opengldemo3.MySurfaceView;

public class MovementPicture {
	int program;//�Զ�����Ⱦ������ɫ������id
    int maPositionHandle;//��ȡ�����ж���λ����������  
    int maTexCoorHandle;//��ȡ�����ж�������������������  
	FloatBuffer mVertexBuffer;// �����������ݻ���
	FloatBuffer fTextureBuffer;//��������buffer
	int vCount;//����ĸ���
	int muMVPMatrixHandle;//��ȡ�������ܱ任��������
	String mVertexShader;// ������ɫ��
	String mFragmentShader;// ƬԪ��ɫ��
	float screenHeight,screenWidth;
    int fuRatioHandle;//�����ε����ű���id

	public MovementPicture(int program,float screenHeight,float screenWidth) {
		// ��ʼ��������������ɫ����
		initVertexData(screenHeight,screenWidth);
		// ��ʼ��shader
		this.program=program;
		initShader();
	}

	// ��ʼ�������������ݵķ���
	public void initVertexData(float height,float wide) {
		// �����������ݵĳ�ʼ��================begin============================
		ArrayList<Float> al_vertex=new ArrayList<Float>();//�洢������Ϣ
		ArrayList<Float> al_texture=new ArrayList<Float>();//�洢������Ϣ
		int levelh=160,levelw=90;
		float everyheight=height/levelh;
		float everywide=wide/levelw;
		for(int i=levelh/2;i>-levelh/2;i--)//ÿ�����ɨ��
		{		//��ǰ�㶥������ߵ������
			float topEdgeFirstPointX=-(levelw/2)*everywide;
			float topEdgeFirstPointY=i*everyheight;
			float topEdgeFirstPointZ=0;
			
			//��ǰ��׶�����ߵ������
			float bottomEdgeFirstPointX=-(levelw/2)*everywide+everywide;
			float bottomEdgeFirstPointY=(i-1)*everyheight;
			float bottomEdgeFirstPointZ=0;
			//---------------����----------------
			float hSpan=1/(float)levelh;//���������ƫ����
			float wSpan=1/(float)levelw;//���������ƫ����
			//��ǰ�㶥�˵�һ������������ز�������
			float topFirstS=0;
			float topFirstT=(levelh/2-i)*hSpan;
			//��ǰ��׶˵�һ�������������ز���
			float bottomFirstS=wSpan;
			float bottomFirstT=(levelh/2-i+1)*hSpan;
			for(int j=0;j<levelw;j++){
				//����
				float topX=topEdgeFirstPointX+j*everywide;
				float topY=topEdgeFirstPointY;
				float topZ=topEdgeFirstPointZ;
				float topS=topFirstS+j*wSpan;
				float topT=topFirstT;
				//���µ�
				float leftBottomX=bottomEdgeFirstPointX+j*everywide;
				float leftBottomY=bottomEdgeFirstPointY;
				float leftBottomZ=bottomEdgeFirstPointZ;
				float leftBottomS=bottomFirstS+j*wSpan;
				float leftBottomT=bottomFirstT;
				//���µ�
				float rightX=topX+everywide;
				float rightY=topY;
				float rightZ=topZ;
				float rightS=topS+wSpan;
				float rightT=topT;
				//��ʱ�����----- ������Ʒ�ʽ
				al_vertex.add(topX);al_vertex.add(topY);al_vertex.add(topZ);
				al_vertex.add(leftBottomX);al_vertex.add(leftBottomY);al_vertex.add(leftBottomZ);
				al_vertex.add(rightX);al_vertex.add(rightY);al_vertex.add(rightZ);
				//-------������Ʒ�ʽ
				al_texture.add(topS);al_texture.add(topT);
				al_texture.add(leftBottomS);al_texture.add(leftBottomT);
				al_texture.add(rightS);al_texture.add(rightT);
				
			}
			for(int j=0;j<levelw;j++){
				//���ϵ�
				float TopX=topEdgeFirstPointX+j*everywide;
				float TopY=topEdgeFirstPointY;
				float TopZ=topEdgeFirstPointZ;
				float TopS=topFirstS+j*wSpan;
				float TopT=topFirstT;
				//�׶˵�
				float leftX=TopX;
				float leftY=TopY-everyheight;
				float leftZ=TopZ;
				float leftS=TopS;
				float leftT=TopT+hSpan;
				//�Ҷ˵�
				float rightX=bottomEdgeFirstPointX+j*everywide;
				float rightY=bottomEdgeFirstPointY;
				float rightZ=bottomEdgeFirstPointZ;
				float rightS=bottomFirstS+j*wSpan;
				float rightT=bottomFirstT;
				//��ʱ�����-----
				al_vertex.add(TopX);al_vertex.add(TopY);al_vertex.add(TopZ);
				al_vertex.add(leftX);al_vertex.add(leftY);al_vertex.add(leftZ);
				al_vertex.add(rightX);al_vertex.add(rightY);al_vertex.add(rightZ);
				
				al_texture.add(TopS);al_texture.add(TopT);
				al_texture.add(leftS);al_texture.add(leftT);
				al_texture.add(rightS);al_texture.add(rightT);
			}
			
		}
		//���ؽ����㻺��
		int vertexSize=al_vertex.size();
		vCount=vertexSize/3;//ȷ������ĸ���	
		float vertexs[]=new float[vertexSize];
		for(int i=0;i<vertexSize;i++)
		{
			vertexs[i]=al_vertex.get(i);
		}
		// ���������������ݻ���
		// vertices.length*4����Ϊһ�������ĸ��ֽ�
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertexSize * 4);
		vbb.order(ByteOrder.nativeOrder());// �����ֽ�˳��
		mVertexBuffer = vbb.asFloatBuffer();// ת��Ϊint�ͻ���
		mVertexBuffer.put(vertexs);// �򻺳����з��붥����������
		mVertexBuffer.position(0);// ���û�������ʼλ��
		// �ر���ʾ�����ڲ�ͬƽ̨�ֽ�˳��ͬ���ݵ�Ԫ�����ֽڵ�һ��Ҫ����ByteBuffer
		// ת�����ؼ���Ҫͨ��ByteOrder����nativeOrder()�������п��ܻ������
		al_vertex=null;

		//���ؽ�������
		int textureSize=al_texture.size();
		float textures[]=new float[textureSize];
		for(int i=0;i<textureSize;i++)
		{
			textures[i]=al_texture.get(i);
		}
		ByteBuffer tbb=ByteBuffer.allocateDirect(textureSize*4);
		tbb.order(ByteOrder.nativeOrder());
		fTextureBuffer=tbb.asFloatBuffer();
		fTextureBuffer.put(textures);
		fTextureBuffer.position(0);
		al_texture=null;
	}

	// ��ʼ��shader
	public void initShader() {

		//��ȡ�����ж���λ����������  
        maPositionHandle = GLES20.glGetAttribLocation(program, "aPosition");
        //��ȡ�����ж�������������������  
        maTexCoorHandle= GLES20.glGetAttribLocation(program, "aTexCoor");
        //��ȡ�������ܱ任��������
        muMVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        //���ű���
        fuRatioHandle = GLES20.glGetUniformLocation(program, "ratio");
	}

	public void drawSelf(int texId,float twistingRatio) {
		//�ƶ�ʹ��ĳ��shader����
   	 	GLES20.glUseProgram(program); 
        //�����ձ任������shader����
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
        //�����ű�������shader����
        GLES20.glUniform1f(fuRatioHandle, twistingRatio); 
        //������λ�����ݴ�����Ⱦ����
        GLES20.glVertexAttribPointer
		(
			maPositionHandle, 
			3, 
			GLES20.GL_FLOAT, 
			false, 
			3*4, 
			mVertexBuffer
		);
		//�������������ݴ�����Ⱦ����
		GLES20.glVertexAttribPointer
		(
			maTexCoorHandle, 
			2, 
			GLES20.GL_FLOAT, 
			false, 
			2*4, 
			fTextureBuffer
		);
		//���ö���λ������
        GLES20.glEnableVertexAttribArray(maPositionHandle);  
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
        //������
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
        //�����������
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);

	}
}
