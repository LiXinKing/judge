uniform mat4 uMVPMatrix; //�ܱ任����
uniform mat4 uMMatrix; //�任����
uniform vec3 uCamera;	//�����λ��
attribute vec3 aPosition;  //����λ��
attribute vec4 aColor;    //������ɫ
varying  vec4 aaColor;  //���ڴ��ݸ�ƬԪ��ɫ���ı���
varying vec3 vPosition;//���ڴ��ݸ�ƬԪ��ɫ���Ķ���λ��
void main()     
{                   
   //�����ܱ任�������˴λ��ƴ˶���λ��         		
   gl_Position = uMVPMatrix * vec4(aPosition,1); 

   aaColor = aColor;//�����յ���ɫ���ݸ�ƬԪ��ɫ��
   
}                      