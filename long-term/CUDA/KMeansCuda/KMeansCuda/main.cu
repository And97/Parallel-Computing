using namespace std;
#include "cuda_runtime.h"
#include "device_launch_parameters.h"
#include <cuda.h>
#include <cmath>
#include <iostream>
#include <fstream>
#include <chrono>
#include <float.h>

using std::chrono::high_resolution_clock;
using std::chrono::duration_cast;
using std::chrono::duration;
using std::chrono::milliseconds;


#define RANGE_COORDINATE_MAX 100000		// range di coordinate nel piano
#define CLUSTER_NUMBER 50			// numero di cluster da creare
#define POINT_NUMBER 5000000     // numero di punti da creare
#define CLUSTER_ATTRIBUTES 4		// numero di caratteristiche di un punto
#define POINT_ATTRIBUTES 3		// <- numero di caratteristiche di un cluster
#define ITERATION 30               // numero massimo di iterazioni
#define THREAD_FOR_BLOCK 256	// numero di thread per blocco (con gt1030 massimo 256)
#define DISTANCE 0 // parametro per la scelta della distanza -> 0: distanza euclidea -> 1: distanza di Manhattan

#define gpuErrchk(ans) { gpuAssert((ans), __FILE__, __LINE__); }

/*
------------------------------------------------------------------------------
	* Metodo per un' ottima visualizzazione degli errori della GPU.
	* Credits: StackOverflow
------------------------------------------------------------------------------
*/
inline void gpuAssert(cudaError_t code, const char* file, int line, bool abort = true)
{
	if (code != cudaSuccess)
	{
		fprintf(stderr, "GPUassert: %s %s %d\n", cudaGetErrorString(code), file, line);
		if (abort) exit(code);
	}
}

/*
------------------------------------------------------------------------------
	* Metodo per generare un numero casuale all'interno di RANGE_COORDINATE_MAX
	* Questo metodo viene utlizzato per generare casualmente i punti.
------------------------------------------------------------------------------
*/

__host__ float random_float() {
	float x = (float)rand() * (float)rand();
	return fmod(x, RANGE_COORDINATE_MAX);
}

/*
------------------------------------------------------------------------------
	* Metodi per il calcolo della distanza fra due punti p1 e p2.
	*
	* distanza euclidea (x1,y1,x2,y2)->sqrt[(x1-x2)^2+(y1-y2)^2]
	*
	* distanza di Manhttan(x1,y1,x2,y2)->|x1-x2|+|y1-y2|
	*
	* per utilizzarli è necessario cambiare la macro DISTANCE
------------------------------------------------------------------------------
*/
__device__ float euclideanDistance(float x1, float y1, float x2, float y2) {
	return sqrt(powf((x1 - x2), 2.0) + powf((y1 - y2), 2.0));
}
__device__ float manhttanDistance(float x1, float y1, float x2, float y2) {
	return fabsf(x1 - x2) + fabsf(y1 - y2);
}
/*
– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - -– – – – – – – – – – – – – – – – – – – – – – – – – –
 * metodo per la generazione casuale della matrici dei punti e dei clusters
 *
 * ogni punto avrà la forma [c,x,y] con:
 *                                      c-> cluster di appartenenza
 *                                      x-> coordinata sull'asse x
 *                                      y-> coordinata sull'asse y
 *
 * ogni cluster avrà la forma [p,n,sx,sy] con:
 *                                          p->indice all'interno della matrice dei punti chenidentifica il centroide
 *                                          n-> numero di punti all'interno del cluster
 *                                          vx-> somma dei valori sull'asse x per il ricalco del centroide
 *                                          vy-> somma dei valori sull'asse y  per il ricalco del centroide
 *
 * i punti verranno salvati in array della forma [c0,c1,...,cn,x1,x2,...,xn,y1,y2,...,yn]
 * i cluster verrano salvati in un array della forma [p0,p1,...,pn,n1,n2,...,nn,vx1,vx2,...,vxn,vy1,vy2,...,vyn]
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - – – – – – – – – – – – – – – – – – – – – – – – – –
*/
__host__ void generatePointCluster(float* points, float* clusters) {
	for (int j = 0; j < POINT_NUMBER; j++) {
		points[0 * POINT_NUMBER + j] = -1;
		points[1 * POINT_NUMBER + j] = random_float();
		points[2 * POINT_NUMBER + j] = random_float();
	}

	for (int j = 0; j < CLUSTER_NUMBER; j++) {
		clusters[0 * CLUSTER_NUMBER + j] = rand() % POINT_NUMBER;
		clusters[1 * CLUSTER_NUMBER + j] = 0;
		clusters[2 * CLUSTER_NUMBER + j] = 0;
		clusters[3 * CLUSTER_NUMBER + j] = 0;
	}
}
/*
– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - - – – – – - -
 * Metodo per assegnare i punti al cluster più vicino, basandosi sulla distanza
 * euclidea o la distanza di Manhattan.
 * Dato un punto da assegnare ad un cluster si va a cercare, tra tutti i cluster
 * disponibili, il cluster il quale centroide minimizza la distanza fra il punto
 * e se stesso
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - – – – – - -
*/

__global__ void assignPointToCluster(float* points, float* clusters) {
	unsigned int point_n = threadIdx.x + blockIdx.x * blockDim.x;
	if (point_n < POINT_NUMBER) {
		float x_cluster, y_cluster = 0;
		//si recuperano le coordinate del punto da assegnare
		float x_point = points[1 * POINT_NUMBER + point_n];
		float y_point = points[2 * POINT_NUMBER + point_n];
		unsigned int best_fitting = 0;
		float old_distance = FLT_MAX;
		float actual_distance = 0;

		for (int i = 0; i < CLUSTER_NUMBER; i++) {
			//si recuperano le coordinate del centroide
			unsigned int centroid_index = clusters[0 * CLUSTER_NUMBER + i];
			x_cluster = points[1 * POINT_NUMBER + centroid_index];
			y_cluster = points[2 * POINT_NUMBER + centroid_index];

			if (DISTANCE == 0) {
				actual_distance = euclideanDistance(x_point, y_point, x_cluster, y_cluster);
			}
			else
			{
				actual_distance = manhttanDistance(x_point, y_point, x_cluster, y_cluster);
			}
			//se la distanza tra il centroide in esame è minore della vecchia distanza
		   //allora si aggiorna il migliore centroide per il punto
			if (actual_distance < old_distance) {
				best_fitting = i;
				old_distance = actual_distance;
			}
		}
		//si  crea la relazione fra il punto e il suo miglior cluster
		points[0 * POINT_NUMBER + point_n] = best_fitting;
		atomicAdd(&clusters[1 * CLUSTER_NUMBER + best_fitting], 1);
	}
}

/*
– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - - – – – – - -
 * metodo utilizzato per calcolare la somma sui due assi di ogni punti all'interno
 * di un cluster
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - – – – – - -
*/
__global__ void calculateValue(float* points, float* clusters) {
	unsigned int point_n = threadIdx.x + blockIdx.x * blockDim.x;
	if (point_n < POINT_NUMBER) {
		unsigned int cluster_n = points[0 * POINT_NUMBER + point_n];
		atomicAdd(&clusters[2 * CLUSTER_NUMBER + cluster_n], points[1 * POINT_NUMBER + point_n]);
		atomicAdd(&clusters[3 * CLUSTER_NUMBER + cluster_n], points[2 * POINT_NUMBER + point_n]);
	}
}

/*
– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - - – – – – - -
 * metodo utilizzato per ricalcolare il centroide all'interno di tutti i cluster
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - – – – – - -
*/
__global__ void recomputeCentroid(float* points, float* clusters) {
	unsigned int cluster_n = threadIdx.x + blockIdx.x * blockDim.x;
	float centroid_x = clusters[2 * CLUSTER_NUMBER + cluster_n] / clusters[1 * CLUSTER_NUMBER + cluster_n];
	float centroid_y = clusters[3 * CLUSTER_NUMBER + cluster_n] / clusters[1 * CLUSTER_NUMBER + cluster_n];
	unsigned int cluster_index = (unsigned int)clusters[0 * CLUSTER_NUMBER + cluster_n];
	//float x = points[1 * POINT_NUMBER + cluster_index];
	points[1 * POINT_NUMBER + cluster_index] = centroid_x;
	points[2 * POINT_NUMBER + cluster_index] = centroid_y;

}
/*
– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - - – – – – - -
 * metodo utilizzato per "eliminare" i punti dal cluster. In pratica si settano
 * i parametri sx,sy,n a 0.
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - – – – – - -
*/
__global__ void removePoint(float* clusters) {
	unsigned int cluster_n = threadIdx.x + blockIdx.x * blockDim.x;
	clusters[1 * CLUSTER_NUMBER + cluster_n] = 0;
	clusters[2 * CLUSTER_NUMBER + cluster_n] = 0;
	clusters[3 * CLUSTER_NUMBER + cluster_n] = 0;
}


__host__ void write(float* points) {
	ofstream file;
	file.open(R"(C:\\Users\\user\\Desktop\\clusterCuda.txt)");
	for (int i = 0; i < POINT_NUMBER; i++) {
		float cluster = points[0 * POINT_NUMBER + i];
		float x = points[1 * POINT_NUMBER + i];
		float y = points[2 * POINT_NUMBER + i];
		file << x << " " << y << " " << cluster << "\n";
	}
	file.close();
}

int main()
{

	srand(time(NULL));
	cout << "----KMmeas algorithm in CUDA--" << endl;
	cout << "Random generation of points and clusters" << endl;

	float* points_host = (float*)malloc(POINT_NUMBER * POINT_ATTRIBUTES * sizeof(float));
	float* clusters_host = (float*)malloc(CLUSTER_NUMBER * CLUSTER_ATTRIBUTES * sizeof(float));
	float* points_device = 0;
	float* clusters_device = 0;
	//generazione e copia dati su device
	generatePointCluster(points_host, clusters_host);
	cudaMalloc(&points_device, POINT_NUMBER * POINT_ATTRIBUTES * sizeof(float));
	cudaMalloc(&clusters_device, CLUSTER_NUMBER * CLUSTER_ATTRIBUTES * sizeof(float));
	cudaMemcpy(points_device, points_host, POINT_NUMBER * POINT_ATTRIBUTES * sizeof(float), cudaMemcpyHostToDevice);
	cudaMemcpy(clusters_device, clusters_host, CLUSTER_NUMBER * CLUSTER_ATTRIBUTES * sizeof(float), cudaMemcpyHostToDevice);
	cout << "Press enter to start..." << endl << endl;
	cin.ignore();

	auto start_time = high_resolution_clock::now();
	for (int i = 0; i < ITERATION; i++) {
		cout << "Iteration--> " << i << endl;
		//assegnamento punti al cluster
		assignPointToCluster << < (POINT_NUMBER + THREAD_FOR_BLOCK - 1) / THREAD_FOR_BLOCK, THREAD_FOR_BLOCK >> > (points_device, clusters_device);
		gpuErrchk(cudaDeviceSynchronize());
		//calcolo del valore su asse x e y utilizzato per il ricalcolo del centroide
		calculateValue << < (POINT_NUMBER + THREAD_FOR_BLOCK - 1) / THREAD_FOR_BLOCK, THREAD_FOR_BLOCK >> > (points_device, clusters_device);
		gpuErrchk(cudaDeviceSynchronize());
		//ricalcolo del centroide
		recomputeCentroid << <1, CLUSTER_NUMBER >> > (points_device, clusters_device);
		gpuErrchk(cudaDeviceSynchronize());
		//rimozione dei punti da ogni cluster
		removePoint << <1, CLUSTER_NUMBER >> > (clusters_device);
		gpuErrchk(cudaDeviceSynchronize());
	}
	gpuErrchk(cudaDeviceSynchronize());

	auto end_time = high_resolution_clock::now();
	duration<double, std::milli> total_time = end_time - start_time;
	cout << endl << "Execution time: " << total_time.count() << " ms" << endl;
	cudaMemcpy(points_host, points_device, POINT_NUMBER * POINT_ATTRIBUTES * sizeof(float), cudaMemcpyDeviceToHost);
	cudaMemcpy(clusters_host, clusters_device, CLUSTER_NUMBER * CLUSTER_ATTRIBUTES * sizeof(float), cudaMemcpyDeviceToHost);
	cudaFree(points_device);
	cudaFree(clusters_device);

	cout << "Write points.... " << endl;
	write(points_host);
	cout << "Press enter to open the plot..." << endl;
	cin.ignore();
	system(R"(gnuplot -p -e "plot 'C:\\Users\\user\\Desktop\\clusterCuda.txt' using 1:2:3 with points palette notitle")");
	return 0;
}
