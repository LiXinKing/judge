package com.example.touch;



public class Vector3 {
	float x;
	float y;
	float z;
	public Vector3(){}
	public Vector3(float x,float y,float z)
	{
		this.x=x;
		this.y=y;
		this.z=z;
	}
	public Vector3(float[] v)
	{
		this.x=v[0];
		this.y=v[1];
		this.z=v[2];
	}
	//�ӷ�
	public Vector3 add(Vector3 v){
		return new Vector3(this.x+v.x,this.y+v.y,this.z+v.z);
	}
	//����
	public Vector3 minus(Vector3 v){
		return new Vector3(this.x-v.x,this.y-v.y,this.z-v.z);
	}

	//�볣�����
	public Vector3 multiK(float k){
		return new Vector3(this.x*k,this.y*k,this.z*k);
	}
	//���
	public void normalize(){
		float mod=module();
		x /= mod;
		y /= mod;
		z /= mod;
	}
	//ģ
	public float module(){
		return (float) Math.sqrt(x*x+y*y+z*z);
	}

}
