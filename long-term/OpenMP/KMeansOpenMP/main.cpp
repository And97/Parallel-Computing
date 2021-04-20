using namespace std;
#include <cmath>
#include <cfloat>
#include <fstream>
#include <iostream>
#include <chrono>

using std::chrono::high_resolution_clock;
using std::chrono::duration_cast;
using std::chrono::duration;
using std::chrono::milliseconds;



#define RANGE_COORDINATE_MAX 100000        //range di coordinate nel piano
#define CLUSTER_NUMBER 100 //numero di cluster da creare
#define POINT_NUMBER 5000   //numero di punti da creare
#define CLUSTER_ATTRIBUTES 4		//numero di caratteristiche di un cluster
#define POINT_ATTRIBUTES 3		//numero di caratteristiche di un punto
#define THREAD_NUMBER 12         //numero di thread con cui eseguire l'algoritmo
#define ITERATION 30               //numero di iterazioni massime
#define DISTANCE 0  //parametro per la scelta del calcolo della distanza:
//-> 1: distanza di Manhattan
//-> 0: distanza euclidea

/*
– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - - -
 * Metodo generare un numero  casuale all'interno di RANGE_COORDINATE_MAX
 * Questo metodo verrà utilizzato per generare casualmente i punti.
– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - - -
*/
float random_float() {
    float x=(float) rand() * (float) rand();
    return fmod(x, RANGE_COORDINATE_MAX);
}

/*
– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - -
 * Metodi per il calcolo della distanza fra due punti p1 e p2.
 *
 * distanza euclidea (x1,y1,x2,y2)-> sqrt[(x1-x2)^2+(y1-y2)^2]
 *
 * distanza di Manhattan (x1,y1,x2,y2)->|x1-x2|+|y1-y2|
 *
 * per utilizzarli è necessario cambiare la macro DISTANCE
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
*/
float euclideanDistance(float x1, float y1, float x2, float y2) {
    return sqrt(pow((x1-x2),2)
                +pow((y1-y2),2));
}

float manhattanDistance(float x1, float y1, float x2, float y2) {

    return abs(x1-x2)+abs(y1-y2);
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
void generatePointCluster(float* points, float* clusters){
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
void assignPointToCluster(float* points, float* clusters) {
#pragma omp parallel for default(none) shared(points, clusters) num_threads(THREAD_NUMBER) schedule(static)
    for (int index=0; index < POINT_NUMBER; index++) {

        float x_cluster, y_cluster = 0;
        //si recuperano le coordinate del punto da assegnare
        float x_point = points[1 * POINT_NUMBER + index];
        float y_point = points[2 * POINT_NUMBER + index];
        unsigned int best_fitting = 0;
        float old_distance = FLT_MAX;
        float actual_distance = 0;

        for (int i = 0; i < CLUSTER_NUMBER; i++) {
            //si recuperano le coordinate del centroide
            unsigned int centroid_index = (int) clusters[0 * CLUSTER_NUMBER + i];
            x_cluster = points[1 * POINT_NUMBER + centroid_index];
            y_cluster = points[2 * POINT_NUMBER + centroid_index];

            if (DISTANCE == 0) {
                actual_distance = euclideanDistance(x_point, y_point, x_cluster, y_cluster);
            } else {
                actual_distance = manhattanDistance(x_point, y_point, x_cluster, y_cluster);
            }
            //se la distanza tra il centroide in esame è minore della vecchia distanza
            //allora si aggiorna il migliore centroide per il punto
            if (actual_distance < old_distance) {
                best_fitting = i;
                old_distance = actual_distance;
            }
        }
        //si  crea la relazione fra il punto e il suo miglior cluster
        points[0 * POINT_NUMBER + index] = (float) best_fitting;
#pragma omp atomic
        clusters[1 * CLUSTER_NUMBER + best_fitting] = clusters[1 * CLUSTER_NUMBER + best_fitting] + 1;
    }
}
/*
– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - - – – – – - -
 * metodo utilizzato per calcolare la somma sui due assi di ogni punti all'interno
 * di un cluster
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - – – – – - -
*/
void calculateValue(float* points, float* clusters){
#pragma omp parallel for default(none) shared(clusters,points) num_threads(THREAD_NUMBER) schedule(static)
    for (int i = 0; i < POINT_NUMBER; i++) {
        unsigned int cluster_n=points[0 * POINT_NUMBER + i];
#pragma omp atomic
        clusters[2*CLUSTER_NUMBER+cluster_n]= clusters[2*CLUSTER_NUMBER+cluster_n]+points[1*POINT_NUMBER+i];
#pragma omp atomic
        clusters[3*CLUSTER_NUMBER+cluster_n]= clusters[3*CLUSTER_NUMBER+cluster_n]+points[2*POINT_NUMBER+i];
    }
}
/*
– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - - – – – – - -
 * metodo utilizzato per ricalcolare il centroide all'interno di tutti i cluster
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - – – – – - -
*/
void recomputeCentroid(float* points, float* clusters) {
#pragma omp parallel for default(none) shared(clusters,points) num_threads(THREAD_NUMBER) schedule(dynamic)
    for (int i = 0; i < CLUSTER_NUMBER; i++) {
        float centroid_x = clusters[2*CLUSTER_NUMBER+i] / clusters[1*CLUSTER_NUMBER+i];
        float centroid_y = clusters[3*CLUSTER_NUMBER+i] / clusters[1*CLUSTER_NUMBER+i];
        unsigned int centroid_index = (int) clusters[0*CLUSTER_NUMBER+i];
        points[1*POINT_NUMBER+centroid_index]= centroid_x;
        points[2*POINT_NUMBER+centroid_index]= centroid_y;

    }
}

/*
– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - - – – – – - -
 * metodo utilizzato per "eliminare" i punti dal cluster. In pratica si settano
 * i parametri sx,sy,n a 0.
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - – – – – - -
*/
void removePoint(float* clusters) {
#pragma omp parallel for default(none) shared(clusters) num_threads(THREAD_NUMBER) schedule(static)
    for (int i = 0; i < CLUSTER_NUMBER; i++) {
        clusters[1*CLUSTER_NUMBER+i] = 0;
        clusters[2*CLUSTER_NUMBER+i] = 0;
        clusters[3*CLUSTER_NUMBER+i] = 0;
    }
}

void write(float* points) {
    ofstream file;
    file.open (R"(C:\Users\Andrea\Desktop\clusterOpenMP.txt)");
    for (int i = 0; i < POINT_NUMBER; i++) {
        float x=points[1*POINT_NUMBER+i];
        float y=points[2*POINT_NUMBER+i];
        float cluster=points[0*POINT_NUMBER+i];
        file<<x<<" "<<y<<" "<<cluster<<endl;
    }
    file.close();
}

float clusters[CLUSTER_ATTRIBUTES][CLUSTER_NUMBER];
float points[POINT_ATTRIBUTES][POINT_NUMBER];

int main() {
    srand(time(NULL));
    float* points=(float*)malloc(POINT_NUMBER * POINT_ATTRIBUTES * sizeof(float));
    float* clusters = (float*)malloc(CLUSTER_NUMBER * CLUSTER_ATTRIBUTES * sizeof(float));
    cout<<"----KMeans Algorithm with OpenMP-----"<<endl<<endl;
    if (THREAD_NUMBER>1){
        cout<<"-->Parallel version execution"<<endl;
    }
    else{
        cout<<"-->Sequential version execution<--"<<endl;
    }
    cout<<"Random generation of points and clusters..."<<endl;
    generatePointCluster(points, clusters);
    //cout<<"Press enter to start... "<<endl;
    //cin.ignore();
    auto start_time = high_resolution_clock::now();
    for(int i = 0; i < ITERATION; i++){
        cout<<"Iteration--> "<<i<<endl;
        assignPointToCluster(points,clusters);
        calculateValue(points, clusters);
        recomputeCentroid(points, clusters);
        removePoint(clusters);
    }
    auto end_time = high_resolution_clock::now();
    duration<double, std::milli> total_time = end_time-start_time;
    cout<<"Execution time : "<<total_time.count()<<" ms"<<endl;
    cout<<"Write point...";
    write(points);
    cout<<endl<<"Press enter to open the plot..."<<endl;
    cin.ignore();
    system(R"(gnuplot -p -e "plot 'C:\Users\Andrea\Desktop\clusterOpenMP.txt' using 1:2:3 with points palette notitle")");
    return 0;
}