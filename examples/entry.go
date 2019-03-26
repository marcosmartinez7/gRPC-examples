package main

import (
	"flag"
	"net/http"

	"github.com/golang/glog"
	"golang.org/x/net/context"
	"github.com/grpc-ecosystem/grpc-gateway/runtime"
	"google.golang.org/grpc"

	gw "./src/main/proto"
)

var (
	echoEndpoint = flag.String("hello_rsk", "localhost:50051", "Hello rsk endpoint")
)

func run() error {
	ctx := context.Background()
	ctx, cancel := context.WithCancel(ctx)
	defer cancel()

	mux := runtime.NewServeMux()
	opts := []grpc.DialOption{grpc.WithInsecure()}

	registerErr := gw.RegisterRSKGreeterHandlerFromEndpoint(ctx, mux, *echoEndpoint, opts)

	if registerErr != nil {
		return registerErr
	}


	return http.ListenAndServe(":8080", mux)
}

func main() {
	flag.Parse()
	defer glog.Flush()

	if err := run(); err != nil {
		glog.Fatal(err)
	}

}

