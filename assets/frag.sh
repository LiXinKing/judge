precision mediump float;
uniform float uR;
varying vec4 aaColor; //���մӶ�����ɫ�������Ĳ���
void main()                         
{

   vec4 finalColor=aaColor;
   //����ƬԪ��ɫֵ
   gl_FragColor=finalColor;
}     